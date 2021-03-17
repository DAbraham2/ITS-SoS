package hu.bme.mit.swsv.itssos.itscentral.logic.impl;

import hu.bme.mit.swsv.itssos.itscentral.logic.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ItsCentralTest {
    private VehicleCommunicator vehicleCommunicator;
    private ItsCentralLogic itsCentralLogic;

    @BeforeEach
    public void init() {
        vehicleCommunicator = mock(VehicleCommunicator.class);
        itsCentralLogic = new ItsCentralLogicImpl(vehicleCommunicator);
    }

    @Test
    void trainProxyRegistersTest() {
        itsCentralLogic.reportTrain(SensorType.PROXIMITY, SensorMessageType.REGISTER);
        verify(vehicleCommunicator).sendBroadcastNotification(NotificationLevel.PASS_SLOWLY);
    }

    @Test
    void trainProxyArriveTest() {
        itsCentralLogic.reportTrain(SensorType.PROXIMITY, SensorMessageType.ARRIVING);

        verify(vehicleCommunicator).sendBroadcastNotification(NotificationLevel.STOP);
    }

    @Test
    void trainProxyLeftTest() {
        itsCentralLogic.reportTrain(SensorType.PROXIMITY, SensorMessageType.LEFT);

        verify(vehicleCommunicator).sendBroadcastNotification(NotificationLevel.PASS_SLOWLY);
    }


}
