import com.convertlab.kafka.KafkaProducerService
import com.convertlab.multitenancy.CurrentTenantThreadLocal
import com.convertlab.multitenancy.TenantHelper
import com.convertlab.multitenancy.resolver.ThreadLocalTenantResolver
import com.convertlab.redis.RedisService
import com.convertlab.transaction.TransactionAwareService


// Place your Spring DSL code here
beans = {
    currentTenant(CurrentTenantThreadLocal)
    tenantResolver(ThreadLocalTenantResolver)
    tenantHelper(TenantHelper, ref('currentTenant'))

    redisService(RedisService)
    //kafka
//    kafkaProducerService(KafkaProducerService, "sample")
//    transactionAwareService(TransactionAwareService, ref('kafkaProducerService'))

}
