package br.com.caelum.jms;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import br.com.caelum.modelo.Pedido;

public class TesteConsumidorTopicoEstoqueSelector {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		
		InitialContext context = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		
		Connection connection = factory.createConnection();
		connection.setClientID("estoque");
		
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		Topic topico = (Topic) context.lookup("loja");
		MessageConsumer consumer = session.createDurableSubscriber(topico, "assinatura-selector", "ebook is null OR ebook=false", false);
		
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {

				ObjectMessage objectMessage = (ObjectMessage)message;
				
				try {
					
					Pedido pedido = (Pedido) objectMessage.getObject();
					System.out.println(pedido.getCodigo());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
		// DLQ - tbm chamada de mensagem venenosa
		// DLQ - Dead Letter Queue - � o nome da fila que as msgs nao lidas sao enviadas (por erro msm)
		// ActiveMQ tenta ler a mensagem 6 vezes
		
				
		new Scanner(System.in).nextLine();
		
		session.close();
		connection.close();
		context.close();
	}
}
