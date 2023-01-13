package vn.com.loyalty.core.service.internal.impl.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.entity.transaction.TransactionMessageEntity;
import vn.com.loyalty.core.repository.TransactionMessageRepository;
import vn.com.loyalty.core.service.internal.TransactionMessageService;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionMessageServiceImpl implements TransactionMessageService {

    private final TransactionMessageRepository transactionMessageRepository;

    @Override
    public TransactionMessageEntity saveMessage(TransactionMessageEntity transactionIncome) {
        return transactionMessageRepository.save(transactionIncome);
    }
}
