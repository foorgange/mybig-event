package itheima;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import java.io.FileInputStream;

/**
 * 阿里云 OSS 上传示例。
 *
 * <p>凭证通过环境变量 OSS_ACCESS_KEY_ID / OSS_ACCESS_KEY_SECRET 注入，
 * 请勿在源码中硬编码任何密钥。</p>
 */
public class Demo {

    public static void main(String[] args) throws Exception {
        // Endpoint 以华北 2（北京）为例，其它 Region 请按实际情况填写。
        String endpoint = System.getenv().getOrDefault(
                "OSS_ENDPOINT", "https://oss-cn-beijing.aliyuncs.com");

        // 从环境变量中获取访问凭证，避免密钥进入版本库。
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");
        if (accessKeyId == null || accessKeyId.isEmpty()
                || accessKeySecret == null || accessKeySecret.isEmpty()) {
            System.err.println("未检测到 OSS_ACCESS_KEY_ID / OSS_ACCESS_KEY_SECRET 环境变量，示例已跳过。");
            return;
        }

        // 填写 Bucket 名称，例如 examplebucket。
        String bucketName = System.getenv().getOrDefault("OSS_BUCKET", "big-event");
        // 填写 Object 完整路径，完整路径中不能包含 Bucket 名称，例如 exampledir/exampleobject.txt。
        String objectName = "001.png";
        // 待上传的本地文件路径。
        String localFile = System.getenv().getOrDefault("OSS_LOCAL_FILE", "001.png");

        // 创建 OSSClient 实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try (FileInputStream inputStream = new FileInputStream(localFile)) {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, objectName, inputStream);
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            System.out.println("上传成功，ETag: " + result.getETag());
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
