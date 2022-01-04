package marmara.app.service;

import marmara.app.model.RegistryConnection;

public interface RegistryHandlings {

    void connectRegistry(RegistryConnection registryConnection, String username, boolean isFirstTime);

    String updateStatus(String iAmOnline);




}
