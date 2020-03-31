import com.redis.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Test {


    public JedisPool jedis(){
        JedisPoolConfig j = new JedisPoolConfig();
        j.setMaxWaitMillis(1000*1000);
        j.setMaxIdle(500);
        j.setMaxTotal(1000);
        JedisPool jedisPool = new JedisPool(j,"127.0.0.1",6379,100*1000);
        return jedisPool;
    }

    @org.junit.jupiter.api.Test
    public void jedisText(){
        JedisPool jedisPool = jedis();
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set("hzm","hzm");
        }catch (Exception e){
            if (jedis == null) {
                jedis.close();
            }
        }finally {
            if (jedis == null) {
                jedis.close();
            }
        }
    }


}
