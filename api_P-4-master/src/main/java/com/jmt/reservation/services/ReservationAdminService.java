package com.jmt.reservation.services;

import com.jmt.reservation.entities.Reservation;
import com.jmt.reservation.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationAdminService {
    private final ReservationRepository reservationRepository;

    public List<Reservation> getList() {
        List<Reservation> items = reservationRepository.findAll();

        //Pagination pagination = new Pagination(page, (int)total, ranges, limit, request);
        //return new ListData<>(items, pagination);

        return items;
    }
}
