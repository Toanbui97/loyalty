package vn.com.loyalty.transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;
import vn.com.loyalty.core.repository.VoucherRepository;
import vn.com.loyalty.core.service.internal.TransactionService;

import java.util.UUID;

//@SpringBootTest
class LoyaltyTransactionApplicationTests {



    VoucherRepository voucherRepository;
    TransactionService transactionService;

    void givenAThenReturnB () {


        VoucherEntity voucherEntity = VoucherEntity.builder().build(); //A
        VoucherEntity voucher = VoucherEntity.builder().build(); //B


        VoucherEntity newVoucher =  voucherRepository.save(voucherEntity);

        assert voucher.equals(newVoucher);

    }

    void setIdTest () {

        VoucherEntity voucher = new VoucherEntity();

        UUID uuid = UUID.randomUUID();

        this.setId(voucher);

        assert voucher.getId() == 1L;


        Mockito.when(transactionService.saveTransaction(new TransactionEntity())).thenReturn(new TransactionEntity());


        this.a(transactionService.saveTransaction(new TransactionEntity()));

    }

    void setId(VoucherEntity entity) {

        entity.setId(1L);
    }


    void a(TransactionEntity transaction) {

    }
}
