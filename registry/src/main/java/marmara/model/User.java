package marmara.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private String username;

    private String password;

    private int checkOnlinePortNumber;

    private int tcpPortNumber;

    private int chatPortNumber;

    private String ipAddress;

}