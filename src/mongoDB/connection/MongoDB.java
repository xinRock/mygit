package mongoDB.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
* @author 孟国庆 e-mail:mengguoqing@mail01.huawei.com
* @version 创建时间：2017年12月21日 下午8:12:25
* 类说明
*/
public class MongoDB {
	
	public static ServerAddress seed1 = new ServerAddress("dds-2ze5d8ceafab36b41.mongodb.rds.aliyuncs.com", 3717);
    public static ServerAddress seed2 = new ServerAddress("dds-2ze5d8ceafab36b42.mongodb.rds.aliyuncs.com", 3717);
    public static String username = "root";
    public static String password = "757025";
    public static String ReplSetName = "mgset-4962171";
    public static String DEFAULT_DB = "test";
    public static String DEMO_DB = "testColl";
    public static String DEMO_COLL = "sss";
    public static MongoClient createMongoDBClient() {
        // 构建Seed列表
        List<ServerAddress> seedList = new ArrayList<ServerAddress>();
        seedList.add(seed1);
        seedList.add(seed2);
        // 构建鉴权信息
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(MongoCredential.createScramSha1Credential(username,
                DEFAULT_DB, password.toCharArray()));
        // 构建操作选项，requiredReplicaSetName属性外的选项根据自己的实际需求配置，默认参数满足大多数场景
        MongoClientOptions options = MongoClientOptions.builder()
                .requiredReplicaSetName(ReplSetName).socketTimeout(2000)
                .connectionsPerHost(1).build();
        return new MongoClient(seedList, credentials, options);
    }
    public static MongoClient createMongoDBClientWithURI() {
        //另一种通过URI初始化
        //mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
        MongoClientURI connectionString = new MongoClientURI("mongodb://" + username + ":" + password + "@" + 
                                                             seed1 + "," + seed2 + "/" + 
                                                             DEFAULT_DB + 
                                                             "?replicaSet=" + ReplSetName);
        return new MongoClient(connectionString);
    }
    public static void main(String args[]) {
        MongoClient client = createMongoDBClient();
        //or
        //MongoClient client = createMongoDBClientWithURI();
        try {
            // 取得Collecton句柄
            MongoDatabase database = client.getDatabase(DEMO_DB);
            MongoCollection<Document> collection = database.getCollection(DEMO_COLL);
            // 插入数据
            Document doc = new Document();
            String demoname = "JAVA:" + UUID.randomUUID();
            doc.append("DEMO", demoname);
            doc.append("MESG", "Hello AliCoudDB For MongoDB");
            collection.insertOne(doc);
            System.out.println("insert document: " + doc);
            // 读取数据
            BsonDocument filter = new BsonDocument();
            filter.append("DEMO", new BsonString(demoname));
            MongoCursor<Document> cursor = collection.find(filter).iterator();
            while (cursor.hasNext()) {
                System.out.println("find document: " + cursor.next());
            }       
        } finally {
            //关闭Client，释放资源
            client.close();
        }
        return ;
    }

}
