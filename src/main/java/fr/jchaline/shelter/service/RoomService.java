package fr.jchaline.shelter.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.jchaline.shelter.config.Constant;
import fr.jchaline.shelter.dao.FloorDao;
import fr.jchaline.shelter.dao.RoomDao;
import fr.jchaline.shelter.dao.RoomTypeDao;
import fr.jchaline.shelter.domain.Floor;
import fr.jchaline.shelter.domain.Room;
import fr.jchaline.shelter.domain.RoomType;

/**
 * @author jeremy
 *
 */
@Service
@Transactional(readOnly = true)
public class RoomService {
	
	@Autowired
	private FloorDao floorDao;

	@Autowired
	private RoomDao dao;

	@Autowired
	private RoomTypeDao roomTypeDao;
	
	public List<Room> list(){
		return dao.findAll();
	}
	
	/**
	 * Check if it's possible to add a room to the shelter.
	 * Vertical : only elevator (if enough place), Horizontal : only if adjoins left/right to another room and enough place.
	 * If you try to add room to cell, this method check right and left to find enough place (if the room take more than 1 cell).
	 * @param idType
	 * @param floor
	 * @param pos
	 * @return
	 */
	public Optional<Set<Integer>> canAddRoom(long idType, long idFloor, int pos) {
		if(isCellEmpty(idFloor, pos)){
			//first, it is an elevator ?
			if (roomTypeDao.findByName(Constant.ELEVATOR).getId().equals(idType)) {
				Optional<Set<Integer>> okHorizontal = isOkHorizontal(idType, idFloor, pos);
				if(hasElevatorVertical(idFloor, pos) || okHorizontal.isPresent()) {
					return Optional.of(Stream.of(pos).collect(Collectors.toSet()));
				}
			}
			//else, another room adjoins left/right ?
			else {
				Optional<Set<Integer>> okHorizontal = isOkHorizontal(idType, idFloor, pos);
				if(isCellEmpty(idFloor, pos) && okHorizontal.isPresent()){
					return okHorizontal;
				}
			}
		}
		return Optional.empty();
	}
	
	/**
	 * If a new room can be add
	 * @param idFloor
	 * @param pos
	 * @return
	 */
	public Optional<Set<Integer>> isOkHorizontal(long idType, long idFloor, int pos) {
		RoomType type = roomTypeDao.findOne(idType);
		Room right = dao.findOneByFloorAndCell(idFloor, pos + 1);
		Room left = dao.findOneByFloorAndCell(idFloor, pos - 1);
		boolean rightEmpty = right == null;
		boolean leftEmpty = left == null;
		
		if (type.getSize() == 1 && (!rightEmpty || !leftEmpty)) {
			return Optional.of(Stream.of(pos).collect(Collectors.toSet()));
		} else {
			if (rightEmpty ^ leftEmpty) {
				return Optional.of(Stream.of(pos, leftEmpty ? pos - 1 : pos + 1).collect(Collectors.toSet()));
			}
		}
		return Optional.empty();
	}
	
	public boolean isCellEmpty(long idFloor, int cell) {
		return dao.findByFloorAndCell(idFloor, cell).isEmpty();
	}
	
	public boolean hasElevatorVertical(long idFloor, int cell) {
		Floor floor = floorDao.findOne(idFloor);
		Floor downstair = floorDao.findByNumber(floor.getNumber() + 1);
		Floor upstair = floorDao.findByNumber(floor.getNumber() - 1);
		Room roomUp = upstair != null ? dao.findOneByFloorAndCell(upstair.getId(), cell) : null;
		Room roomDown = downstair != null ? dao.findOneByFloorAndCell(downstair.getId(), cell) : null;
		return roomUp != null && Constant.ELEVATOR.equals(roomUp.getRoomType().getName()) || roomDown != null && Constant.ELEVATOR.equals(roomDown.getRoomType().getName());
	}
	
	/**
	 * Merge two room if possible, and save in db
	 * @param idA The id of the first room
	 * @param idB The id of the second room
	 * @return the new merged room
	 */
	@Transactional
	public Room merge(long idA, long idB) {
		final Room right = dao.getOne(idA);
		final Room left = dao.getOne(idB);
		right.setSize(right.getSize() + left.getSize());
		dao.save(right);
		dao.delete(left);
		return right;
	}
	
	@Transactional
	public Floor construct(int floorNumber, int cell, String typeName) {
		Floor floor = floorDao.findByNumber(floorNumber);
		RoomType type = roomTypeDao.findByName(typeName);
		
		Optional<Set<Integer>> canAddRoom = canAddRoom(type.getId(), floor.getId(), cell);
		canAddRoom.ifPresent(value -> {
			Room room = new Room(type, value);
			floor.getRooms().add(room);
			//TODO : merge if possible !
			dao.save(room);
		});
		
		return floorDao.save(floor);
	}

	public Room find(long id) {
		return dao.findOne(id);
	}

	public List<RoomType> findAllType() {
		return roomTypeDao.findAll();
	}
	
}
