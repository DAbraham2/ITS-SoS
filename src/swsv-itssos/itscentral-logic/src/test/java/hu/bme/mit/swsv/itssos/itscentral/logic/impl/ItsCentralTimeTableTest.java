package hu.bme.mit.swsv.itssos.itscentral.logic.impl;

import hu.bme.mit.swsv.itssos.itscentral.logic.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ItsCentralTimeTableTest {

    private VehicleCommunicator vehicleCommunicator;
    private ItsCentralLogic itsCentralLogic;

    @BeforeEach
    public void init(){
        vehicleCommunicator = mock(VehicleCommunicator.class);
        itsCentralLogic = new ItsCentralLogicImpl(vehicleCommunicator);
    }

    @Test
    public void TrainMightArriveTest(){
        itsCentralLogic.reportTrain(SensorType.TIMETABLE, SensorMessageType.ARRIVING);

        verify(vehicleCommunicator).sendBroadcastNotification(NotificationLevel.TRAIN_MIGHT_ARRIVE);
    }

    @Test
    public void TTDTrainIsLeavingTest(){
        itsCentralLogic.reportTrain(SensorType.TIMETABLE, SensorMessageType.LEFT);
        verify(vehicleCommunicator).sendBroadcastNotification(NotificationLevel.LOOK_AROUND);
    }
}
