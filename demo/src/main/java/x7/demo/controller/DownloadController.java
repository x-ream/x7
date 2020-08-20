package x7.demo.controller;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/download")
public class DownloadController {

    private static Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @RequestMapping(value = "/test/{whNo}/{startAt}/{endAt}/{passportId}/{token}", method = RequestMethod.GET)
    public void test(
            @PathVariable String whNo,
            @PathVariable long startAt,
            @PathVariable long endAt,
            @PathVariable String passportId,
            @PathVariable String token,
            HttpServletRequest request,
            HttpServletResponse response) {


//        if (!passportId.equals(token)) {
//            return ViewEntity.toast("xxxxxx");
//        }

        List<String> urlList = new ArrayList<>();
        urlList.add("https://contract-ruhr.oss-cn-beijing.aliyuncs.com/QT_1594888410296446.pdf");
        urlList.add("https://contract-ruhr.oss-cn-beijing.aliyuncs.com/QT_1594893635289440.pdf");

        String title = "";
        try {
            title = new String("出库质检报告.zip".getBytes(), "ISO-8859-1");
        }catch (Exception e){

        }
        response.setHeader("content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + title);

        ZipOutputStream zos = null;

        try {
            zos = new ZipOutputStream(response.getOutputStream());
            zos.setComment("出库质检报告");

            byte[] buffer = null;
            for (String str : urlList) {

                if (StringUtils.isBlank(str)) continue;

                String name = str.contains("/") ? (str.substring(str.lastIndexOf("/") + 1)) : str;

                InputStream is = null;
                ByteArrayOutputStream output = null;
                try {
                    URL url = new URL(str);
                    is = url.openStream();
                    buffer = new byte[4096];
                    output = new ByteArrayOutputStream();
                    int n = 0;
                    while (-1 != (n = is.read(buffer))) {
                        output.write(buffer, 0, n);
                    }

                } catch (Exception e) {

                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception e) {

                        }
                    }
                }

                if (output == null)
                    continue;
                logger.info("download: " + name);
                ZipEntry ze = null;
                try {
                    ze = new ZipEntry(name);
                    zos.putNextEntry(ze);
                    zos.write(output.toByteArray());
                    zos.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (output != null){
                        output.close();
                    }
                    if (zos != null) {
                        try {
                            zos.closeEntry();
                        }catch (Exception e) {

                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (zos != null) {
                    zos.finish();
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }

            try {
                response.getOutputStream().close(); //???
            }catch (Exception e){
//                e.printStackTrace();
            }

            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }

        }


    }
}
