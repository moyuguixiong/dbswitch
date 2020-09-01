##dsswitch-数据源自动切换springboot组件
####一、主要功能
#####1、支持跨数据库的自动切库，建议service层下，一个数据库，对应一个包。不同的包路径，配置不同的数据库DataSource，访问不同包的方法，会自动切换数据库，例如：  
     me.jin.service.order     商品数据库的service。  
     me.jin.service.log       日志数据库的service。
     给不同的包配置不同的数据库(每个包路径，支持配置读、写两个数据源)。
#####2、支持相同数据库下，读写数据源自动切换，默认是数据库的读数据源。
     a、me.jin.service.order.OrderService     商品库的service包的OrderService类的方法，如果方法名以add、insert、update、alter、delete、remove开头，会自动切换到写数据源。  
     b、如果方法名称不是以上6个单词开头，但是需要使用写数据源，在方法上打上@ForceDBWrite注解，会自动切换到写数据源。  
     c、方法名打上@Transactional注解，会自动切换为写数据源。
#####3、支持多个数据库混合调用，自动切换数据库，自动切换回主包数据库，并支持主包的spring事务提交(只支持主包数据库的多sql语句事务提交，其他数据库只支持单语句事务)。
#####4、支持springboot自动配置，接入简单。
     a、项目引入dsswitch-spring-boot-starter-0.0.1.jar
     b、配置PackageDataSourceConfig类的bean对象。
     c、application.properties配置dsswitch.enable=true
####二、使用举例：以支付流程举例,
#####1、两个数据库，支付库pay，日志库pay_log
#####2、service层包结构
     me.jin.service.Pay.PayService 
     me.jin.service.Log.PayLogService
#####3、支付方法伪代码
     public class PayServiceImpl{
     @Autowired
     private PayLogService payLogService;
     
     @Autowired
     private PayMapper payMapper;
     
     @Transactional
     public void pay(){
       //自动切换为Pay库的写数据源
        payMapper.update();//冻结余额
        //自动切换为Log库的写数据。
        payLogService.addFriozenLog();//日志库插入余额冻结日志
        //自动切换为Pay库的写数据源
        payMapper.update();//消费余额
        //自动切换为Log库的写数据。
        payLogService.addConsumerLog();//日志库插入余额消费日志
     }
     //方法支持完成，会自动清除当前数据源
     }
      
     