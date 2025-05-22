/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rabbitmq.days.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class OrderProducer {
    private static final String QUEUE_NAME = "orders";

    public static void main(String[] args) throws Exception {
        // 1) Configura la conexión a RabbitMQ localhost
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection conn = factory.newConnection();
             Channel channel = conn.createChannel()) {

            // 2) Declara la cola 'orders' (no duradera, no exclusiva)
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            // 3) Crea un ejemplo de Order
            Order order = new Order("1001", "Laptop", 2);
            String json = order.toJson(); 

            // 4) Publica el JSON en la cola
            channel.basicPublish("", QUEUE_NAME, null, json.getBytes("UTF-8"));
            System.out.println("[Producer] Sent order: " + json);
        }
    }
}
