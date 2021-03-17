package hu.bme.mit.swsv.itssos.itscentral;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import eu.arrowhead.client.skeleton.provider.ItsCentralApplicationInitListener;
import eu.arrowhead.common.Defaults;
import eu.arrowhead.common.Utilities;
import hu.bme.mit.swsv.itssos.vehicle.SendNotificationRequestDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static eu.arrowhead.common.CommonConstants.ECHO_URI;
import static hu.bme.mit.swsv.itssos.itscentral.ItsCentralProviderInternalConstants.ITSCENTRAL_URI;
import static hu.bme.mit.swsv.itssos.itscentral.ItsCentralProviderInternalConstants.REPORT_TRAIN_FULL_URI;
import static hu.bme.mit.swsv.itssos.itscentral.logic.NotificationLevel.PASS_SLOWLY;
import static hu.bme.mit.swsv.itssos.itscentral.logic.SensorMessageType.REGISTER;
import static hu.bme.mit.swsv.itssos.itscentral.logic.SensorType.PROXIMITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ItsCentralIntegrationTest {

    // =================================================================================================
    // members
    private static final Logger logger = LogManager.getLogger(ItsCentralIntegrationTest.class);

    private static final String SEND_NOTIFICATION_URI = "/vehicle-communicator/send-notification";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    private ItsCentralApplicationInitListener initListener;

    @Autowired
    private ItsCentralController controller;

    @ClassRule
    public static WireMockClassRule serviceRegistryMock = new WireMockClassRule(
            options().notifier(new ConsoleNotifier(true)).port(Defaults.DEFAULT_SERVICE_REGISTRY_PORT));
    @ClassRule
    public static WireMockClassRule orchestratorMock = new WireMockClassRule(
            options().notifier(new ConsoleNotifier(true)).port(Defaults.DEFAULT_ORCHESTRATOR_PORT));
    @ClassRule
    public static WireMockClassRule vehicleCommunicatorMock = new WireMockClassRule(
            options().notifier(new ConsoleNotifier(true)).port(8889));

    // =================================================================================================
    // methods

    // -------------------------------------------------------------------------------------------------

    @BeforeClass
    public static void setupClass() {
        logger.info("setupClass: START");

        vehicleCommunicatorMock.stubFor(post(urlPathEqualTo(SEND_NOTIFICATION_URI))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("OK")));

        logger.info("setupClass: END");
    }

    @Before
    public void setup() {
        logger.info("setup: START");

        initListener.init(applicationContext);
        controller.init();

        logger.info("setup: END");
    }

    // -------------------------------------------------------------------------------------------------
    @Test
    public void testItsCentralEcho() {
        logger.info("testItsCentralEcho: START");
        // Arrange

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(ITSCENTRAL_URI + ECHO_URI, String.class);

        // Assert
        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), containsString("Got it!"));
        logger.info("testItsCentralEcho: END");
    }

    @Test
    public void testProximityRegister() {
        logger.info("testProximityRegister: START");
        // Arrange
        ReportTrainRequestDto payload = new ReportTrainRequestDto(PROXIMITY, REGISTER);

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(REPORT_TRAIN_FULL_URI, payload, String.class);

        // Assert
        assertThat(response.getStatusCodeValue(), is(200));
        assertThat(response.getBody(), containsString("OK"));

        String expectedNotification = Utilities.toJson(new SendNotificationRequestDto(null, PASS_SLOWLY));
        vehicleCommunicatorMock.verify(1, postRequestedFor(urlEqualTo(SEND_NOTIFICATION_URI))
                .withRequestBody(equalToJson(expectedNotification))
                .withHeader("Content-Type", equalTo("application/json")));
        logger.info("testProximityRegister: END");
    }
}