package com.event_planner.event_planner;

import com.event_planner.event_planner.controller.UserController;
import com.event_planner.event_planner.model.User;
import jakarta.transaction.Transactional;
import org.hibernate.validator.internal.constraintvalidators.bv.AssertTrueValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Objects;

@SpringBootTest
@Transactional
class EventPlannerApplicationTests {
	@Autowired
	UserController userController;
//    @Autowired
//    private AssertTrueValidator assertTrueValidator;

	@Test
	void contextLoads() {
			User user = new User();
			user.setEmail("test@test.com");
			user.setPasscode("1234");
			user.setName("test");
			userController.register(user);
			Assert.notNull(user, "User is null");
			Assert.isTrue(Objects.requireNonNull(userController.getUserbyEmail("test@test.com").getBody()).getName().equals("test"),"Not k k?");
//			assertTrueValidator.equals("test".equals(userController.getUserbyEmail("test@test.com").getBody().getName()));
//			if (userController.getUserbyEmail("test@test.com") != null){
//
//				System.out.println(userController.getUserbyEmail("test@test.com").getBody().getName());
//			}
//			else{
//				System.out.println("esuat");
//			}
//		System.out.println(userController.getUserbyEmail("test@test.com").getBody().getName());

	}

}
