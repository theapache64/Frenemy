package com.theah64.frenemy.web.servlets;

import com.theah64.frenemy.web.database.Connection;
import com.theah64.frenemy.web.exceptions.RequestException;
import com.theah64.frenemy.web.utils.Response;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * Created by theapache64 on 11/18/2015,12:10 AM.
 */

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/upload"})
@MultipartConfig
public class UploadServlet extends AdvancedBaseServlet {


    private static final String KEY_FILE = "file";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setGETMethodNotSupported(resp);
    }


    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return null;
    }

    @Override
    protected void doAdvancedPost() throws IOException, ServletException, RequestException, JSONException {


        //Yes,it's a valid data type
        final Part dataFilePart = getHttpServletRequest().getPart(KEY_FILE);

        if (dataFilePart != null) {

            //Saving file
            final Part voiceFilePart = getHttpServletRequest().getPart("file");
            String fileDownloadPath = null;
            if (voiceFilePart != null) {


                final File dataDir = new File("/var/www/html/frenemy_data");

                System.out.println(dataDir.getAbsolutePath());
                if (!dataDir.exists()) {
                    dataDir.mkdirs();
                    dataDir.setReadable(true, false);
                    dataDir.setExecutable(true, false);
                    dataDir.setWritable(true, false);
                }

                final String fileName = Paths.get(voiceFilePart.getSubmittedFileName()).getFileName().toString();
                final File voiceFile = new File(dataDir.getAbsolutePath() + File.separator + fileName);

                final InputStream is = voiceFilePart.getInputStream();
                final FileOutputStream fos = new FileOutputStream(voiceFile);
                int read = 0;
                final byte[] buffer = new byte[1024];

                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }

                fos.flush();
                fos.close();
                is.close();

                voiceFile.setReadable(true, false);
                voiceFile.setExecutable(true, false);
                voiceFile.setWritable(true, false);

                fileDownloadPath = voiceFile.getAbsolutePath().split("/html")[1];
                getWriter().write(new Response("File uploaded", "download_link", getBaseUrl() + fileDownloadPath).getResponse());
            }

        } else {
            throw new RequestException("File missing from request");
        }

    }

    public static String getBaseUrl() {
        return Connection.isDebugMode() ? "http://localhost" : "http://theapache64.xyz";
    }
}
