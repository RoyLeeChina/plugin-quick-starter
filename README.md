# plugin-quick-starter
AD Hub应用开发模板项目

前端开发指引
从上面的项目模板开始，可以快速开始应用前端代码的开发。此处的前端是指应用管理段的界面，即从DM Hub应用市场打开应用时访问的界面。产品自动流程和微页面模板相关的前端开发请参考功能开发文档章节。

具体的开发步骤和流程，请参考下面的文档：

[**_应用前端开发指引_**](https://api-docs.convertlab.com/v2/openhub/frontend)

后端开发指引
从上面的项目模板开始，可以快速搭建应用后端的服务。该部分包括项目的结构说明、需要配置的点、示例代码以及开发中的注意事项等。

具体的开发步骤和流程，请参考下面的文档：

**_应用后端开发指引_** （[参考](https://api-docs.convertlab.com/v2/openhub/backend)）

1、在启动之前，请修改grails-app/conf/application.yml文件中的数据库连接信息，如果不需要连接数据库则可以去掉该项配置：

~~~dataSource:
pooled: true
jmxExport: true
driverClassName: com.mysql.jdbc.Driver
username: root
password: root
dbCreate: none
url: jdbc:mysql://localhost:3306/sample?useUnicode=yes&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull
~~~
将dataSource中的url的数据库名sample修改为要连接的数据库名称。

2、启动本地的redis服务和kafka服务。

如果不需要对应的服务，可以按照下面的两个步骤移除相关服务：

 (a) 修改grails-app/conf/spring/resources.groovy文件进行移除（注释相关的服务声明即可），该文件的源码如下所示：
~~~
beans = {
currentTenant(CurrentTenantThreadLocal)
tenantResolver(ThreadLocalTenantResolver)
tenantHelper(TenantHelper, ref('currentTenant'))

    redisService(RedisService)
    //kafka
//    kafkaProducerService(KafkaProducerService, "sample")
//    transactionAwareService(TransactionAwareService, ref('kafkaProducerService'))


}
~~~
 该文件中声明了需要初始化的服务。

 (b) 然后再移除grails-app/init/sample/BootStrap.groovy文件中相关的服务声明和调用，该文件源码如下所示：
~~~
class BootStrap {

    //Define your services here
 
    def redisService
    def kafkaProducerService
 
    //def sampleSendMmsService
 
    def init = { servletContext ->
        log.info("==> bootstrap start==")
        DateTimeZone.setDefault(DateTimeZone.UTC)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        HttpClient.service = "extmms"
 
        redisService.init()
//        kafkaProducerService.init()
 
        //sampleSendMmsService.start()
 
    }
    def destroy = {
//        kafkaProducerService.close()
        //sampleSendMmsService.shutdown()
    }
}
~~~
 在该文件中移除redisService、kafkaProducerService的声明和调用。

 3、执行如下命令启动服务：
~~~
cd plugin-quick-starter
./gradlew build
./gradlew bootrun
~~~
  项目启动成功后，console里会显示服务的运行信息，如：
~~~
Grails application running at http://localhost:29004 in environment: development
~~~
  运行的端口为application.yml里指定的端口。

  服务启动成功后，访问如下接口：
~~~
http://localhost:29004/ping
~~~
  如果看到返回的信息：pong，则说明服务启动成功。

#### **1. 项目结构**

   应用后端服务的项目结构如下：
~~~
plugin-quick-starter
|-gradle
|-grails-app
|       |-assets
|       |-conf
|           |-kafka
|               |-topics.json
|           |-rabbit
|               |-exchanges.json
|               |-queues.json
|           |-spring
|               |-resources.groovy
|           |-application.groovy
|           |-application.yml
|           |-logback.groovy
|       |-controllers
|           |-app
|               |-OauthController.groovy
|           |-health
|               |-PingController.groovy
|           |-interceptor
|               |-LogInterceptor.groovy
|               |-TenantInterceptor.groovy
|           |-sample
|               |-SampleController.groovy
|           |-UrlMappings.groovy
|       |-domain
|       |-i18n
|       |-init
|           |-sample
|               |-Application.groovy
|               |-BootStrap.groovy
|       |-jobs
|       |-migrations
|           |-changelog-initial.groovy
|           |-changelog.groovy
|       |-services
|           |-app
|           |-common
|           |-consumer
|       |-utils
|       |-views
|-src
|   |-前端文件
|-build.gradle
|-gradle.properties
|
~~~
主要目录和文件说明如下：

* conf/application.yml里是项目相关的设置信息，包括端口，数据库连接以及各种环境变量。
* conf/kafka/topics.json里面配置了kafka的topic，启动服务时会根据这里的配置来注册topic。
* conf/rabbit目录下时RabbitMQ的exchange和queue的配置。项目里不建议再使用rabbit，可以用kafka来代替。
* conf/spring/resources.groovy里定义了服务启动时要初始化的service。
* controllers/UrlMappings.groovy里定义里rest接口的访问路径和对应controller以及action的对应关系。
* contollers/interceptor目录下定义了拦截器，请注意拦截器里面设定的拦截范围。
* init/sample/BootStrap.groovy中定义了服务启动时要初始化的服务，以及服务停止时要关闭的服务。
* jobs目录下是各种定时job。
* migrations目录下定义了对数据库进行DDL操作的各种changeset，在使用时应该注意不要直接修改已经执行过的changeset，如果需要变更，应该通过新的changeset来进行，否则服务启动会失败。
* services目录下是应用里各种service的定义。
* build.gradle文件中是gradle相关的配置，主要是项目的各种依赖。

#### **2. 开始开发**

#####    2.1 修改模板项目

   以plugin-quick-starter为模板开始开发项目，需要对模板项目进行一系列对改动。主要改动点如下：

    1. 将项目的名称plugin-quick-starter更改为自己项目的名称。
    2. 更改build.gradle，其中的group和war.baseName默认值均为sample，请更改为自己的服务名称。其中baseName为build后生成的war包的名称，后续配置中的服务名称一般与这个相同，请不要包含特殊字符。
    3. 更改conf/logback.groovy，将其中出现sample的地方替换为服务名称。
    4. 更改conf/spring/resources.groovy，如果项目中需要发送kafka消息，则将kafkaProducerService初始化中的第二个参数sample更改为项目的服务名称。
    5. 将controllers, domain, init目录下的package名称sample改成项目中的package名称。请注意，更改里package名称之后，package下面的class中第一行的package定义要同步修改。
    6. 更改conf/application.yml文件里的设置，主要包括：
        a. 更改dataSource.url里面的数据库名称，将sample改为要连接的数据库，并确保本地对应的数据库存在。
        b. 更改grails.codegen.defaultPackage，将sample改为实际的package名称，确保与init目录下面的package一致。

更改完成后，请本地启动服务，是否能正常启动。在启动之前，请先删除项目根目录下生成的build目录。然后运行./gradlew bootrun来启动服务。

##### 2.2 使用kafka收发消息

如果项目中需要使用到消息队列，建议使用kafka。具体步骤如下：

在grails-app/conf/kafka/topics.json中注册topic，格式如下：
~~~

[
{
"name": "sample_topics",
"consumerServices": [
"sample"
],
"partitions": 20
}
]
~~~

其中name即为topic的名称，consumerService更改为项目的服务名称，partition为该topic指定的分区数。

添加自定义的topic之后，请把示例中的topic删除。

在application.yml中定义topic相关变量，如下所示：
~~~

kafka:
sampleTopic:
topic: "sample_topics"
groupId: "sample_group"
numConsumers: 10
~~~

需要指定topic，group和消费者数量。一般消费者数量不要超过partition的数量，否则会有部分消费者一直处于空闲状态。

如果要消费kafka消息，定义消息的消费者，可参考grails-app/services/consumer/SampleSendService.groovy，源码如下：
~~~
class SampleSendService extends KafkaConsumerManager {

    @Value('${kafkaServer.bootstrap.servers}')
    String bootstrapServers
 
    @Value('${kafka.flowSend.topic}')
    String topic
 
    @Value('${kafka.flowSend.groupId}')
    String groupId
 
    @Value('${kafka.templateSend.numConsumers}')
    Integer numConsumers
 
    def redisService
 
    @Override
    void processKafkaMessage(String key, Map message) {
        log.info("flowsend receive message is:${message}")
 
        def flag = "mms::flowSend:${key}"
        def notBlocked = false
         
        redisService.withRedis { Jedis jedis ->
            def time = System.currentTimeMillis().toString()
            notBlocked = jedis.set(flag, time, "nx", "ex", 5 * 60)
        }
 
        if (!notBlocked) {
            log.info("mms flow mass send ${key} already consumed by others")
            return
        }
        // Deal with message here
    }
}
~~~
其中@Value注入的变量均来自application.yml中的定义。

然后在grails-app/init/{package}/BootStrap.groovy中添加对该服务的启动和销毁操作，如下所示：
~~~
class BootStrap {

    def sampleSendService
 
    def init = { servletContext ->
        sampleSendService.start()
    }
    def destroy = {
        sampleSendService.shutdown()
    }
}
~~~
如果要发送或生产kafka消息，可以直接使用kafkaProducerService。由于该服务已经在resources.groovy中定义，并在BootStrap.groovy中启动，可以直接使用，用法如下：
~~~
def kafkaProducerService

@Value('${kafka.sampleSend.topic}')
String sampleTopic

def send(){
def messageBody = [
type:"xxx",
templateId: templateId,
campaignUuid: campaignUuid,
tenantId: tenantId,
customerId: customerId
]

    def key = messageBody.customerId + '_' + messageBody.templateId
    kafkaProducerService.send(sampleTopic, "${key}", messageBody)
}
~~~

#### **3. 注意事项**

   正确定义interceptor。以grails-app/controllers/interceptor/LogInterceptor.groovy为例：
~~~

LogInterceptor() {
matchAll()
.except(uri:"/dist/**")
.except(uri:"/scripts/**")
.except(uri:"/public/**")
.except(uri:"/static/**")
.except(controller: 'ping', action: 'pong')
}
~~~

其中定义了需要排除的拦截，即对except中定义对uri或controller不做拦截，主要是对静态资源的一些请求。如果不排除，静态资源如html页面等可能无法正确访问。

使用job时，合理定义job启动的周期，避免job启动过于频繁消耗过多的系统资源。如果确实需要频繁启动，如果一次job执行未完成时已经到了下一个启动周期，默认会重新启动一个job的实例，从而造成混乱。为了避免这种情况的发生，可以在job的定义中增加concurrent = false，如下所示：
~~~

class TokenRefreshJob {
concurrent = false

    static triggers = {
        simple name:"TokenRefreshJob", startDelay: 2000 , repeatInterval: 60000 //refresh every 60s
    }
 
    def execute(){
         
    }
}
~~~
