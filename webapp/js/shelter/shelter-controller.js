var app = angular.module( "shelterModule", ['datatables', 'ngResource'] )
		
app.controller("shelterController", function( $scope, $rootScope, shelterService ) {
	
	$scope.updateGame = function() {
		shelterService.getData("/game/get").then(thenDoUpdateGame)
		$scope.showDisplayRoom = false
		$scope.showConstructRoom = false
	}
	
	$scope.updateDwellers = function() {
		shelterService.getData("/dweller/list").then(function(data){
			$scope.dwellers = data
		})
	}
	
	$scope.actionRoom = function(floor, room) {
		$scope.showDisplayRoom = false
		$scope.showConstructRoom = false
		if(room.id > 0){
			$scope.showDisplayRoom = true
			shelterService.getData("/room/"+room.id).then(function(data){
				$scope.displayedRoom = data
			})
		} else {
			$scope.showConstructRoom = true
			shelterService.getData("/room/types").then(function(data){
				$scope.roomtype = data
				$scope.constructFloor = floor.number
				$scope.constructCell = room.cells[0]
			})
		}
	}
	
	$scope.upgradeRoom = function(room) {
		shelterService.postData("/room/upgrade/"+room.id, {}).then(function(data){
			$scope.updateGame()
		})
	}
	
	$scope.construct = function(floor, cell, type) {
		shelterService.postData("/room/construct", {floor:floor, cell:cell, type:type}).then(function(data){
			$scope.updateGame()
		})
	}
	
	// when request is ok, processing server data
	function thenDoUpdateGame(game) {
		var floors = game.shelter.floors
		fillEmptySpace(floors)
		$scope.floors = floors
		$scope.money = game.shelter.money
		$scope.food = game.shelter.food
		$scope.water = game.shelter.water
	}
	
	// add "empty room" between reals rooms,
	// use to interact with floor and construct room
	function fillEmptySpace(floors) {
		var keys = Object.keys(floors)
		for (var key in keys) {
			var floor = floors[key]
			var cellList = Array.apply(null, {length: floor.size}).map(Number.call, Number)
			var used = floor.rooms.map(function(v, i) {
				return v.cells
			})
			.reduce(function(previousValue, currentValue, index, array) {
				return previousValue.concat(currentValue)
			})
			cellList.forEach(function(e) {
				if($.inArray(e, used) == - 1){
					var emptySpace = {id:-1, 'size':1, floor:key, 'cells':[e], 'roomType':{'name':'vide'}}
					floor.rooms.push(emptySpace)
				}
			})
		}
		return floors
	}
	
	$rootScope.$on('dropEvent', function(evt, dweller, room) {
		shelterService.postData("/room/" + room + "/assign/" + dweller, {}).then(function(data){
			$scope.updateDwellers()
		})
    });
	
	// function use to order room in floor
	$scope.roomOrder = function(room){
	    return room.cells[0]
	}

	// call when app is loaded
	angular.element(document).ready(function () {
		$scope.updateGame()
		$scope.updateDwellers()
    });
})