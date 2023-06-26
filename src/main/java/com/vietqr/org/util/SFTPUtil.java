package com.vietqr.org.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jcraft.jsch.*;

public class SFTPUtil {

    public void readRemoteFile() {
        String host = "112.78.1.220";
        int port = 22;
        String username = "root";
        String password = "4G01T1r3!Ab1";
        String remoteFilePath = "/usr/data/transactions/in/RV_20230614_TC.txt";

        JSch jsch = new JSch();

        try {
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(channelSftp.get(remoteFilePath)))) {
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    count++;
                    System.out.println("\nLine " + count + ": ");
                    String[] data = line.split("\\|");
                    for (String part : data) {
                        System.out.println(part);
                    }
                }
            }

            channelSftp.disconnect();
            session.disconnect();

        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        }
    }
}