package vn.com.loyalty.core.service.internal.impl.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.entity.transaction.TransactionIncomeEntity;
import vn.com.loyalty.core.repository.TransactionIncomeRepository;
import vn.com.loyalty.core.service.internal.TransactionIncomeService;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionIncomeServiceImpl implements TransactionIncomeService {

    private final TransactionIncomeRepository transactionIncomeRepository;

    @Override
    public TransactionIncomeEntity saveTransactionIncome(TransactionIncomeEntity transactionIncome) {
        return transactionIncomeRepository.save(transactionIncome);
    }
}
