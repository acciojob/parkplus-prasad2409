package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        ParkingLot parkingLot;
        try{
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }
        catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        User user;
        try {
            user = userRepository3.findById(userId).get();
        }
        catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        List<Spot> spotList = parkingLot.getSpotList();
        int minCost = Integer.MAX_VALUE;
        Spot minCostSpot = null;

        for(Spot spot : spotList){
            if(!spot.getOccupied()){
                SpotType spotWheels = spot.getSpotType();
                int cost;
                if((spotWheels == SpotType.TWO_WHEELER) && numberOfWheels <= 2){
                    cost = spot.getPricePerHour()*timeInHours;
                    if(cost < minCost){
                        minCost = cost;
                        minCostSpot.setSpotType(SpotType.TWO_WHEELER);
                        minCostSpot = spot;
                    }

                }
                else if((spotWheels == SpotType.FOUR_WHEELER) && numberOfWheels <=4){
                    cost = spot.getPricePerHour()*timeInHours;
                    if(cost < minCost){
                        minCost = cost;
                        minCostSpot.setSpotType(SpotType.FOUR_WHEELER);
                        minCostSpot = spot;
                    }
                }
                else {
                    cost = spot.getPricePerHour()*timeInHours;
                    if(cost < minCost){
                        minCost = cost;
                        minCostSpot.setSpotType(SpotType.OTHERS);
                        minCostSpot = spot;
                    }
                }
            }
        }
        if(minCostSpot == null){
            throw new Exception("Cannot make reservation");
        }
        minCostSpot.setOccupied(true);

        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(minCostSpot);
        reservation.setUser(user);

        user.getReservationList().add(reservation);
        minCostSpot.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(minCostSpot);

        return reservation;
    }
}
