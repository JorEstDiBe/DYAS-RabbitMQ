/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rabbitmq.days.rabbitmq;

import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;

public class OrderConsumer {
    private static final String QUEUE_NAME = "orders";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection conn = factory.newConnection();
             Channel channel = conn.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("[Consumer] Waiting for orders...");

            DeliverCallback callback = (tag, delivery) -> {
                // 1) Obtenemos el JSON del cuerpo del mensaje
                String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("[Consumer] Received JSON: " + json);

                try {
                    // 2) Deserializamos a Order
                    Order order = Order.fromJson(json);

                    // 3) Simulamos validación de inventario
                    if (order.getQuantity() <= 5) {
                        System.out.printf("[Consumer] Order %s for %d×%s processed successfully.%n",
                            order.getOrderId(), order.getQuantity(), order.getProduct());
                    } else {
                        System.out.printf("[Consumer] Order %s exceeds stock for %s.%n",
                            order.getOrderId(), order.getProduct());
                    }
                } catch (Exception ex) {
                    System.err.println("[Consumer] Error parsing order: " + ex.getMessage());
                }
            };

            // Auto-ack = true (para este ejemplo simple)
            channel.basicConsume(QUEUE_NAME, true, callback, tag -> { });
        }
    }
}

