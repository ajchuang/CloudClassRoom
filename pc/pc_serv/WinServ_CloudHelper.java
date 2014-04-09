// @lfred: it's a helper class which encapsulated cloud things here
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.ListPartsRequest;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PartListing;
import com.amazonaws.services.s3.model.UploadPartRequest;
*/

public class WinServ_CloudHelper {
    
    static long sm_uploadPartSize = 500 * 1024;
    
    static String sm_bucketName = "CloudClassRoom";
    static long   sm_partSize   = 10*1024*1024;

    // filePath: local file path
    // keyName: remove file name
    public static boolean uploadFile (String filePath, String keyName) {
        
        boolean retVal = true;
        
        /*
        AmazonS3 s3Client = 
            new AmazonS3Client (new PropertiesCredentials (
        		LowLevelJavaUploadFile.class.getResourceAsStream (
                    "./sys/AwsCredentials.properties")));        

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
            	sm_uploadPartSize = Math.min (sm_uploadPartSize, (contentLength - filePosition));
            	
                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest ()
                    .withBucketName (sm_bucketName).withKey(keyName)
                    .withUploadId (initResponse.getUploadId()).withPartNumber(i)
                    .withFileOffset (filePosition)
                    .withFile (file)
                    .withPartSize (sm_uploadPartSize);

                // Upload part and add response to our list.
                partETags.add (s3Client.uploadPart (uploadRequest).getPartETag ());

                filePosition += sm_uploadPartSize;
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
        */
        
        return retVal;
    }
    
    public static boolean downloadFile (String fName) {
        return true;
    }

}