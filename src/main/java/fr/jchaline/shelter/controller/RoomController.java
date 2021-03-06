package fr.jchaline.shelter.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.jchaline.shelter.domain.Floor;
import fr.jchaline.shelter.domain.Room;
import fr.jchaline.shelter.domain.RoomType;
import fr.jchaline.shelter.json.Construct;
import fr.jchaline.shelter.service.RoomService;

@RestController
@RequestMapping(value = "/room", method = RequestMethod.GET)
public class RoomController extends AbstractShelterController {
	
	@Autowired
	private RoomService service;
	
	@RequestMapping("/list")
	public List<Room> list(){
		return service.list();
	}

	@RequestMapping("/types")
	public List<RoomType> types(){
		return service.findAllType();
	}
	
	@RequestMapping("/{id}")
	public Room find(@PathVariable long id) {
		return service.find(id);
	}

	@RequestMapping(value = "/construct", method = RequestMethod.POST)
	public Floor construct(@RequestBody @Valid Construct construct) {
		return service.construct(construct.getFloor(), construct.getCell(), construct.getType());
	}

	@RequestMapping(value = "/upgrade/{id}", method = RequestMethod.POST)
	public Room upgrade(@PathVariable long id) {
		return service.upgrade(id);
	}

	@RequestMapping(value = "/{id}/assign/{idDweller}", method = RequestMethod.POST)
	public Room assign(@PathVariable long id, @PathVariable long idDweller) {
		return service.assign(id, idDweller);
	}
	
}
