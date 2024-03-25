package com.kernelsquare.alertapi.domain.alert.service;

import com.kernelsquare.alertapi.domain.alert.dto.AlertDto;
import com.kernelsquare.domainmongodb.domain.alert.command.AlertCommand;
import com.kernelsquare.domainmongodb.domain.alert.entity.Alert;

import java.util.List;

public interface AlertService {
    List<AlertDto.MessageResponse> findAllAlerts(AlertCommand.FindCommand command);

    void sendToClient(Alert alert);

    void sendToStorage(Alert alert);
}
