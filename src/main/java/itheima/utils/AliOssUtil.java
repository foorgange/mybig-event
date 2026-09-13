package itheima.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;

import java.io.InputStream;

/**
 * 阿里云 OSS 上传工具。
 *
 * <p>访问凭证从环境变量 OSS_ACCESS_KEY_ID / OSS_ACCESS_KEY_SECRET 读取，
 * 请勿在源码中硬编码任何密钥。</p>
 */
public class AliOssUtil {

    /** Endpoint 以华北 2（北京）为例，可通过环境变量覆盖。 */
    private static final String ENDPOINT = System.getenv().getOrDefault(
            "OSS_ENDPOINT", "https://oss-cn-beijing.aliyuncs.com");

    private static final String ACCESS_KEY_ID = System.getenv("OSS_ACCESS_KEY_ID");
    private static final String ACCESS_KEY_SECRET = System.getenv("OSS_ACCESS_KEY_SECRET");

    /** Bucket 名称，例如 examplebucket。 */
    private static final String BUCKET_NAME = System.getenv().getOrDefault(
            "OSS_BUCKET", "big-event");

    public static String uploadFile(String objectName, InputStream in) throws Exception {

        if (ACCESS_KEY_ID == null || ACCESS_KEY_ID.isEmpty()
                || ACCESS_KEY_SECRET == null || ACCESS_KEY_SECRET.isEmpty()) {
            throw new IllegalStateException(
                    "未配置 OSS 访问凭证，请设置环境变量 OSS_ACCESS_KEY_ID 与 OSS_ACCESS_KEY_SECRET");
        }

        // 创建 OSSClient 实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        String url = "";
        try {
            // 创建 PutObjectRequest 对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectName, in);

            // 上传文件。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            // url 组成: https://bucket名称.区域节点/objectName
            url = "https://" + BUCKET_NAME + "."
                    + ENDPOINT.substring(ENDPOINT.lastIndexOf("/") + 1) + "/" + objectName;
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

        return url;
    }
}
