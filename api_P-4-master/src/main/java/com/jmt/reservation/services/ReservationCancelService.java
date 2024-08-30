package com.jmt.reservation.services;

import com.jmt.global.exceptions.UnAuthorizedException;
import com.jmt.member.MemberUtil;
import com.jmt.member.entities.Member;
import static com.jmt.reservation.constants.ReservationStatus.*;
import com.jmt.reservation.constants.ReservationStatus;
import com.jmt.reservation.entities.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationCancelService {
    private final ReservationInfoService infoService;
    private final ReservationStatusService statusService;
    private final MemberUtil memberUtil;

    public void cancel(Long orderNo) {
        Reservation item = infoService.get(orderNo);
        Member member = memberUtil.getMember();
        Member rMember = item.getMember();
        if (!member.getEmail().equals(rMember.getEmail())) {
            throw new UnAuthorizedException();
        }

        ReservationStatus status = item.getStatus();

        if (status == APPLY || status == START) {
            statusService.change(orderNo, CANCEL);
        } else if (status == CONFIRM) {
            statusService.change(orderNo, REFUND);
        }
    }
}
