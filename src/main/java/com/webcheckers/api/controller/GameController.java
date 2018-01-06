package com.webcheckers.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webcheckers.api.domain.Movement;
import com.webcheckers.api.domain.Player;
import com.webcheckers.api.service.MessageService;

@RestController
@RequestMapping("/api")
public class GameController {

	@Autowired
	private MessageService messageService;
	
	@RequestMapping("/test")
	public Movement hello(@RequestParam("table") String table) {
		
		System.out.println("Incoming request for table " + table);
		
		Movement movement = new Movement(Player.WHITE, "B3", "D1");
		movement.addKilled("C2");
		
		messageService.sendMessage();
		
		return movement;
	}
}
