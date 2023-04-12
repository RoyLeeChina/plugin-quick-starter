package sample

import com.convertlab.rest.HttpClient
import org.joda.time.DateTimeZone

class BootStrap {

    //Define your services here

    def redisService
    def kafkaProducerService

    def sampleSendService

    def init = { servletContext ->
        log.info("==> bootstrap start==")
        DateTimeZone.setDefault(DateTimeZone.UTC)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        HttpClient.service = "sample"

//        redisService.init()
//        kafkaProducerService.init()

//        sampleSendService.start()

    }
    def destroy = {
//        kafkaProducerService.close()
//        sampleSendService.shutdown()
    }
}
