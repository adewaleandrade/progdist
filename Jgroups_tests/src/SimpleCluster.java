import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.protocols.JDBC_PING;
import org.jgroups.util.Util;

import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class SimpleCluster extends ReceiverAdapter {
	
	Conexao conn;
    JChannel channel;
    String user_name = System.getProperty("user.name", "n/a");
    final List<String> state = new LinkedList<String>();

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        String line = msg.getSrc() + ": " + msg.getObject();
        System.out.println(line);
        synchronized(state) {
            state.add(line);
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized(state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        List<String> list = (List<String>)Util.objectFromStream(new DataInputStream(input));
        synchronized(state) {
            state.clear();
            state.addAll(list);
        }
        System.out.println("received state (" + list.size() + " messages in chat history):");
        for(String str: list) {
            System.out.println(str);
        }
    }

    private void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("DBCluster");
        
        //conecting with postgres
        conn = new Conexao("PostgreSql","cachoeira.inema.intranet","5432","meioambiente","postgres","123456");
        conn.conect();
        //insert into acao (dtc_criacao, ind_excluido, dsc_acao) values (now(), false, 'cluster1')
        
        //utilizar JDBC_PING
        JDBC_PING jping = new JDBC_PING();
        
/*
		new JDBC_PING() 
		.setValue("connection_url",	Config.get("DB.dbdriver") + ':' + Config.get("DB.dburl") + '/' + Config.get("DB.dbname")) 
		.setValue("connection_username", Config.get("DB.dbuser")) 
		.setValue("connection_password", Config.get("DB.dbpwd")) 
		.setValue("connection_driver",	"org.postgresql.Driver") 
		.setValue("initialize_sql", "CREATE TABLE IF NOT EXISTS JGROUPSPING ( own_addr varchar(200) NOT NULL, cluster_name varchar(200)	NOT NULL, ping_data bytea DEFAULT NULL, PRIMARY KEY (own_addr, cluster_name) )")  
*/       
        

        channel.getState(null, 10000);
        eventLoop();
        
        channel.close();
    }

    private void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> "); System.out.flush();
                String line = in.readLine().toLowerCase();
                String origLine = line;
                if(line.startsWith("quit") || line.startsWith("exit")) {
                    break;
                }
                line="[" + user_name + "] " + line;
                Message msg = new Message(null, null, line);
                channel.send(msg);
                conn.query(origLine);
            }
            catch(Exception e) {
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new SimpleCluster().start();
    }          
}
