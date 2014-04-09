// @lfred: it's a helper class which encapsulated cloud things here

public class WinServ_CloudHelper {
    
    static String sm_bucketName = "CloudClassRoom";
    static String sm_awsKey     = "*** Provide-Key-Name ***";
    static long   sm_partSize   = 10*1024*1024;

    public static boolean uploadFile (String fName) {
        
        /*
        String existingBucketName   = sm_bucketName; 
        String keyName              = "*** Provide-Key-Name ***";
        String filePath             = "*** Provide-File-Path ***";   
        
        AmazonS3 s3Client = 
            new AmazonS3Client (new PropertiesCredentials (
        		LowLevelJavaUploadFile.class.getResourceAsStream ("AwsCredentials.properties")));        

        // Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
        List<PartETag> partETags = new ArrayList<PartETag>();

        // Step 1: Initialize.
        InitiateMultipartUploadRequest initRequest = new 
            InitiateMultipartUploadRequest (existingBucketName, keyName);
            
        InitiateMultipartUploadResult initResponse = 
            s3Client.initiateMultipartUpload(initRequest);

        File file = new File(filePath);
        long contentLength = file.length();
        long partSize = sm_partSize; // Set part size to 5 MB.

        try {
            // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Last part can be less than 5 MB. Adjust part size.
            	partSize = Math.min(partSize, (contentLength - filePosition));
            	
                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                    .withBucketName(existingBucketName).withKey(keyName)
                    .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                    .withFileOffset(filePosition)
                    .withFile(file)
                    .withPartSize(partSize);

                // Upload part and add response to our list.
                partETags.add(
                		s3Client.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }

            // Step 3: complete.
            CompleteMultipartUploadRequest compRequest = new 
                         CompleteMultipartUploadRequest(
                                    existingBucketName, 
                                    keyName, 
                                    initResponse.getUploadId(), 
                                    partETags);

            s3Client.completeMultipartUpload(compRequest);
        } catch (Exception e) {
            s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    existingBucketName, keyName, initResponse.getUploadId()));
        }
        */
    }
    
    public static boolean downloadFile (String fName) {
    }

}