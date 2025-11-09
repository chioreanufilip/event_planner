package com.event_planner.event_planner;

//import org.junit.jupiter.api.Assertions.*;
import com.event_planner.event_planner.controller.UserController;
import com.event_planner.event_planner.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import com.event_planner.event_planner.model.Participant;
import com.event_planner.event_planner.controller.ParticipantController;
import com.event_planner.event_planner.model.Organizer;
import java.util.Objects;
import com.event_planner.event_planner.controller.AuthController;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EventPlannerApplicationTests {
	@Autowired
	UserController Controller;
	@Autowired
	ParticipantController participantController;
	@Autowired
	AuthController AuthController;
//    @Autowired
//    private AssertTrueValidator assertTrueValidator;

	@Test
	void contextLoads() {
		Organizer organizer = new Organizer();
		organizer.setPassword("12");
		organizer.setEmail("test1@test.com");
		organizer.setName("Bula");
		Participant participant = new Participant();
		participant.setName("test");
		participant.setEmail("test@test.com");
		participant.setPassword("1234");
		AuthController.registerParticipant(participant);
//		participantController.
//		participant.setIsParticipant(1);
//			User user = new User();
//			user.setEmail("test@test.com");
//			user.setPasscode("1234");
//			user.setName("test");
//			userController.register(user);
			Assert.notNull(Controller.getUserbyEmail(participant.getEmail()), "User is null");
//			System.out.println(userController.getUserbyEmail(participant.getEmail()).getBody().getName());
		Integer participantid = Controller.getUserbyEmail(participant.getEmail()).getBody().getIdUser();
		Assert.isTrue(Objects.requireNonNull(Controller.getUserbyEmail("test@test.com").getBody()).getName().equals("test"),"Not k k?");
		participant.setName("test2");
		Controller.updateParticipant(Long.valueOf(participantid), participant);
		assertEquals(Controller.getUserbyEmail("test@test.com").getBody().getName(), "test2");

		Controller.deleteUser(Long.valueOf(Controller.getUserbyEmail(participant.getEmail()).getBody().getIdUser()));
		ResponseEntity<User> participantAfterDeletion =  Controller.getUserbyId(Long.valueOf(participantid));
//			assertTrue(participantAfterDeletion.equals(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
		assertEquals(HttpStatus.NOT_FOUND, participantAfterDeletion.getStatusCode());
//			assertTrueValidator.equals("test".equals(userController.getUserbyEmail("test@test.com").getBody().getName()));
//			if (userController.getUserbyEmail("test@test.com") != null){
//
//				System.out.println(userController.getUserbyEmail("test@test.com").getBody().getName());
//			}
//			else{
//				System.out.println("esuat");
//			}
//		System.out.println(userController.getUserbyEmail("test@test.com").getBody().getName());
//	Controller.registerOrganizer(organizer);
//	organizer.setName("Bula1");
//	Integer idOrganizer = userController.getUserbyEmail(organizer.getEmail()).getBody().getIdUser();
//	assertEquals(userController.getUserbyEmail(organizer.getEmail()).getBody().getName(), "Bula1");
//	userController.deleteUser(Long.valueOf(idOrganizer));
//		ResponseEntity<User> organizerAfterDeletion =  userController.getUserbyId(Long.valueOf(idOrganizer));
////			assertTrue(participantAfterDeletion.equals(new ResponseEntity<>(HttpStatus.NOT_FOUND)))
//		assertEquals(HttpStatus.NOT_FOUND, organizerAfterDeletion.getStatusCode());
	}

}
