package com.webcheckers.api.persistence;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.webcheckers.api.persistence.domain.GameStarter;
import com.webcheckers.api.persistence.domain.LightGame;

@Component
public class RestClientImpl implements RestClient{

	private String WAKE_UP_URL = "https://webcheckersrest.herokuapp.com/state/wake-up/";
	private String LOAD_URL = "https://webcheckersrest.herokuapp.com/state/load/{id}";
	private String SAVE_URL = "https://webcheckersrest.herokuapp.com/state/save/";
	private String CREATE_URL = "https://webcheckersrest.herokuapp.com/state/create/";
	
	@Autowired
	private RestTemplate checkersRest;
	
	@Override
	public LightGame loadGame(int id) {

		return checkersRest.getForObject(LOAD_URL, LightGame.class, String.valueOf(id));
	}

	@Override
	public boolean saveGame(LightGame game) {
		
		ResponseEntity<Object> response = checkersRest.postForEntity(SAVE_URL, game, Object.class);
		return HttpStatus.OK.equals(response.getStatusCode());
	}

	@Override
	@PostConstruct
	public boolean wakeUp() {
		
		try {
			ResponseEntity<Object> response = checkersRest.getForEntity(WAKE_UP_URL, Object.class, new Object[0]);
			return HttpStatus.OK.equals(response.getStatusCode());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean createGame(GameStarter gameStarter) {
		
		ResponseEntity<Object> response = checkersRest.postForEntity(CREATE_URL, gameStarter, Object.class);
		return HttpStatus.OK.equals(response.getStatusCode());
	}
}
