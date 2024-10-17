package com.vietqr.org.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class LogReaderUtil {
    static final String host = EnvironmentUtil.getSftpHosting();
    static final int port = EnvironmentUtil.getSftpPort();
    static final String username = EnvironmentUtil.getSftpUsername();
    static final String password = EnvironmentUtil.getSftpPassword();

    public static List<String> readLogFile(String date) {
        List<String> result = new ArrayList<>();
        JSch jsch = new JSch();
        try {
            String remoteFilePath = "";
            if (date == null || date.trim().isEmpty()) {
                remoteFilePath = "/opt/tomcat/logs/vietqr.log";
            } else {
                remoteFilePath = "/opt/tomcat/logs/vietqr.log" + "." + date;
            }
            //
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            if (remoteFilePath != null && !remoteFilePath.trim().isEmpty()) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(channelSftp.get(remoteFilePath)))) {
                    String line;
                    // int count = 0;
                    while ((line = reader.readLine()) != null) {
                        // count++;
                        // //System.out.println("\nLine " + count + ": " + line);
                        result.add(line);
                    }
                    // if (contentBuilder != null) {
                    // result = contentBuilder.toString();
                    // }
                }
            }

            channelSftp.disconnect();
            session.disconnect();

        } catch (Exception e) {
            //System.out.println(e.toString());
        }
        return result;
    }

}
