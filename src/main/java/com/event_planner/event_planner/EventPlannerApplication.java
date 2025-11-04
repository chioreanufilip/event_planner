package com.event_planner.event_planner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.event_planner.event_planner.controller.*;
import com.event_planner.event_planner.service.*;
import com.event_planner.event_planner.model.*;
import com.event_planner.event_planner.repository.*;
@SpringBootApplication
public class EventPlannerApplication {


	public static void main(String[] args) {
		SpringApplication.run(EventPlannerApplication.class, args);
	}
//	User user = new User();

//	@Autowired
//	RepoUser repoUser;
//	@Autowired
//	UserService userService;
//	EventPlannerApplication eventPlannerApplication = new EventPlannerApplication();


}
