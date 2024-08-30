package com.jmt.reservation.controllers;

import com.jmt.global.ListData;
import com.jmt.global.Utils;
import com.jmt.global.exceptions.BadRequestException;
import com.jmt.global.rests.JSONData;
import com.jmt.payment.services.PaymentConfig;
import com.jmt.reservation.entities.Reservation;
import com.jmt.reservation.services.ReservationInfoService;
import com.jmt.reservation.services.ReservationPayService;
import com.jmt.reservation.services.ReservationSaveService;
import com.jmt.reservation.validators.ReservationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationValidator validator;
    private final ReservationSaveService saveService;
    private final ReservationPayService payService;
    private final Utils utils;
    private final ReservationInfoService infoService;

    @PostMapping("/apply")
    public JSONData apply(@Valid @RequestBody RequestReservation form, Errors errors) {

        validator.validate(form, errors);

        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        Reservation reservation = saveService.save(form);

        // 결제시 필요한 데이터
        PaymentConfig config = payService.getConfig(reservation.getOrderNo());

        return new JSONData(config);
    }

    public void payProcess() {
        
    }

    /**
     * 목록 조회
     *
     * @param search
     */
    @GetMapping("/list")
    public JSONData list(@ModelAttribute ReservationSearch search) {

        ListData<Reservation> data = infoService.getList(search);
        return new JSONData(data);
    }

    /**
     * 상세 조회 (개별 조회)
     * @param orderNo
     * @return
     */

    @GetMapping("/info/{orderNo}")
    public JSONData info(@PathVariable("orderNo") Long orderNo) {
        Reservation item = infoService.get(orderNo);
        return new JSONData(item);
    }
}
