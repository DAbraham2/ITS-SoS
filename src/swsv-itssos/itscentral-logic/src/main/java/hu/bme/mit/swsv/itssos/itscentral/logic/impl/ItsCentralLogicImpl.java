package hu.bme.mit.swsv.itssos.itscentral.logic.impl;

import hu.bme.mit.swsv.itssos.itscentral.logic.*;

import java.util.HashSet;
import java.util.Set;

import static hu.bme.mit.swsv.itssos.itscentral.logic.NotificationLevel.PASS_SLOWLY;
import static hu.bme.mit.swsv.itssos.itscentral.logic.NotificationLevel.STOP;

// DO NOT ADD NEW PUBLIC MEMBERS TO THIS CLASS
public class ItsCentralLogicImpl implements ItsCentralLogic {
    private final VehicleCommunicator vehicleCommunicator;

    private Set<String> registeredVehicles;

    // DO NOT MODIFY THE SIGNATURE OF THE CONSTRUCTOR
    public ItsCentralLogicImpl(VehicleCommunicator vehicleCommunicator) {
        this(vehicleCommunicator, new HashSet<>());
    }

    // DO NOT MODIFY THE SIGNATURE OF THE CONSTRUCTOR
    // separate constructor for testing
    protected ItsCentralLogicImpl(VehicleCommunicator vehicleCommunicator, Set<String> registeredVehicles) {
        this.vehicleCommunicator = vehicleCommunicator;
        this.registeredVehicles = registeredVehicles;
    }

    @Override
    public void reportTrain(SensorType sensorType, SensorMessageType messageType) {
        if (sensorType == SensorType.PROXIMITY) {
            switch (messageType) {
                case REGISTER:
                    vehicleCommunicator.sendBroadcastNotification(PASS_SLOWLY);
                    break;
                case ARRIVING:
                    vehicleCommunicator.sendBroadcastNotification(STOP);
                    break;
                case LEFT:
                    vehicleCommunicator.sendBroadcastNotification(PASS_SLOWLY);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown SensorMessageType");
            }
        }
    }

    @Override
    public void reportVehicle(String registrationNumber, VehicleMessageType message) {
        switch (message) {
            case ARRIVING:
                registeredVehicles.add(registrationNumber);
                break;
            case LEFT:
                registeredVehicles.remove(registrationNumber);
                break;
            default:
                throw new IllegalArgumentException("Unknown VehicleMessageType");
        }
    }
}
