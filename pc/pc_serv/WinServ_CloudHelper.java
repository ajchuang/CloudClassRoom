// @lfred: it's a helper class which encapsulated cloud things here
import java.io.*;
import java.util.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;

public class WinServ_CloudHelper {
    
    final static String sm_bucketName = "CloudClassRoom";
    
    final static int sm_uploadPartSize = 500 * 1024;
    final static int sm_downloadPartSize = 10 * 1024;
    
    // filePath: local file path
    // keyName: remove file name
    public static boolean uploadFile (String filePath, String keyName) throws Exception {
        
        boolean retVal = true;
        int uploadPartSize = sm_uploadPartSize;
        
        File sf = new File (WinServ_SysParam.getSecretFileS3 ());
        PropertiesCredentials pc = new PropertiesCredentials (sf);
        AmazonS3 s3Client = new AmazonS3Client (pc);        

        // Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
        List<PartETag> partETags = new ArrayList<PartETag>();

        // Step 1: Initialize.
        InitiateMultipartUploadRequest initRequest = new 
            InitiateMultipartUploadRequest (sm_bucketName, keyName);
            
        InitiateMultipartUploadResult initResponse = 
            s3Client.initiateMultipartUpload (initRequest);

        File file = new File (filePath);
        long contentLength = file.length ();
        
        try {
            // Step 2: Upload parts.
            long filePosition = 0;
            
            for (int i = 1; filePosition < contentLength; i++) {
                
                // Last part can be less than 5 MB. Adjust part size.
            	uploadPartSize = (int) Math.min (uploadPartSize, (contentLength - filePosition));
            	
                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest ()
                    .withBucketName (sm_bucketName).withKey(keyName)
                    .withUploadId (initResponse.getUploadId()).withPartNumber(i)
                    .withFileOffset (filePosition)
                    .withFile (file)
                    .withPartSize (uploadPartSize);

                // Upload part and add response to our list.
                partETags.add (s3Client.uploadPart (uploadRequest).getPartETag ());

                filePosition += uploadPartSize;
            }

            // Step 3: complete.
            CompleteMultipartUploadRequest compRequest = 
                new CompleteMultipartUploadRequest (
                    sm_bucketName,
                    keyName,
                    initResponse.getUploadId (),
                    partETags);

            s3Client.completeMultipartUpload (compRequest);
            
        } catch (Exception e) {

            // OOOps - the file upload fails            
            s3Client.abortMultipartUpload (new AbortMultipartUploadRequest (
                sm_bucketName, keyName, initResponse.getUploadId ()));
                
            retVal = false;
        }
        
        return retVal;
    }
    
    public static boolean downloadFile (String localPath, String bucketName, String keyName) {
        
        // local var
        boolean ret = true;
        WinServ.logInfo ("downloadFileFromS3: " + localPath + ":" + bucketName + ":" + keyName);
        
        int rCounr = 0;
        byte[] buf = new byte[sm_downloadPartSize];
        
        try {
            AmazonS3 s3Client = 
                new AmazonS3Client (new PropertiesCredentials (
                    WinServ_CloudHelper.class.getResourceAsStream (
                        WinServ_SysParam.getSecretFileS3 ())));
                
            S3Object remoteObj = s3Client.getObject (new GetObjectRequest (bucketName, keyName));
            ObjectMetadata metaObj = remoteObj.getObjectMetadata ();
             
            WinServ.logInfo (
                "Content-Type: " + metaObj.getContentType () + ":" + 
                "Content-Size: " + metaObj.getContentLength ());
                
            // write the object to a local file.
            InputStream reader = new BufferedInputStream (remoteObj.getObjectContent ());
            
            File outfile = new File (localPath);      
            OutputStream writer = new BufferedOutputStream (new FileOutputStream (outfile));

            while ((rCounr = reader.read (buf)) != -1 ) {
                WinServ.logInfo ("counter " + rCounr);
                writer.write (buf, 0, rCounr);
            }
            
            writer.flush ();
            writer.close ();
            
        } catch (AmazonServiceException ase) {
            WinServ.logErr (
                "Caught an AmazonServiceException, which " +
                "means your request made it " +
                "to Amazon S3, but was rejected with an error response " +
                "for some reason.");
            WinServ.logErr ("Error :    " + ase.getMessage ());
            WinServ.logErr ("HTTP Status Code: " + ase.getStatusCode ());
            WinServ.logErr ("AWS Error Code:   " + ase.getErrorCode ());
            WinServ.logErr ("Error Type:       " + ase.getErrorType ());
            WinServ.logErr ("Request ID:       " + ase.getRequestId ());
            ret = false;
        } catch (AmazonClientException ace) {
            WinServ.logErr (
                "Caught an AmazonClientException, which means "+
                "the client encountered " +
                "an internal error while trying to " +
                "communicate with S3, " +
                "such as not being able to access the network.");
            WinServ.logErr ("Error Message: " + ace.getMessage());
            ret = false;
        } catch (Exception eee) {
            eee.printStackTrace ();
            ret = false;
        }
        
        return ret;
    }
}