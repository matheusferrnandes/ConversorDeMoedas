package org.example;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Scanner;

public class ConversorDeMoedas {

    public static void main(String[] args) throws IOException {

        Boolean running = true;
        do {
            // HashMap para armazenar os códigos de conversão de moeda
            HashMap<Integer, String> codigosConversao = new HashMap<Integer, String>();

            // Adicionando códigos de conversão
            codigosConversao.put(1, "USD");
            codigosConversao.put(2, "BRL");
            codigosConversao.put(3, "EUR");
            codigosConversao.put(4, "CAD");

            Integer from, to;
            String fromCode, toCode;
            double amount;

            Scanner sc = new Scanner(System.in);

            System.out.println("Bem vindo ao conversor de moedas");

            // Solicitação da moeda de origem
            System.out.println("Converter de:");
            System.out.println("1: USD (Dólar Americano) \t 2: BRL (Real Brasileiro) \t 3: EUR (Euro) \t 4: CAD (Dólar Canadense)");
            from = sc.nextInt();
            while (from < 1 || from > 4) {
                System.out.println("Por favor, selecione uma moeda válida!");
                System.out.println("1: USD (Dólar Americano) \t 2: BRL (Real Brasileiro) \t 3: EUR (Euro) \t 4: CAD (Dólar Canadense)");
                from = sc.nextInt();
            }
            fromCode = codigosConversao.get(from);

            // Solicitação da moeda de destino
            System.out.println("Para:");
            System.out.println("1: USD (Dólar Americano) \t 2: BRL (Real Brasileiro) \t 3: EUR (Euro) \t 4: CAD (Dólar Canadense)");
            to = sc.nextInt();
            while (to < 1 || to > 4) {
                System.out.println("Por favor, selecione uma moeda válida!");
                System.out.println("1: USD (Dólar Americano) \t 2: BRL (Real Brasileiro) \t 3: EUR (Euro) \t 4: CAD (Dólar Canadense)");
                to = sc.nextInt();
            }
            toCode = codigosConversao.get(to);

            // Solicitação do valor a ser convertido
            System.out.println("Valor que deseja converter:");
            amount = sc.nextFloat();

            // Chamada para a função de envio da solicitação HTTP GET
            sendHttpGetRequest(fromCode, toCode, amount);

            // Pergunta se deseja fazer outra conversão
            System.out.println("Gostaria de fazer outra conversão?");
            System.out.println("1: Sim \t Pressione qualquer outra tecla para sair");
            if (sc.nextInt() != 1) {
                running = false;
            }

        } while (running);

        System.out.println("A equipe ONE agradece por usar o conversor de moedas!");
    }

    // Função para enviar uma solicitação HTTP GET para obter as taxas de câmbio
    private static void sendHttpGetRequest(String fromCode, String toCode, double amount) throws IOException {
        DecimalFormat f = new DecimalFormat("00.00");
        String GET_URL = "https://v6.exchangerate-api.com/v6/b4630abeb3c8a6ee518ec6ad/latest/" + fromCode;

        try {
            URL url = new URL(GET_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                // Lendo a resposta da solicitação HTTP
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Convertendo a resposta para um objeto JSON
                JSONObject obj = new JSONObject(response.toString());

                // Obtendo a taxa de câmbio da moeda de origem para a moeda de destino
                double exchangeRate = obj.getJSONObject("conversion_rates").getDouble(toCode);

                // Calculando o valor convertido
                double convertedAmount = amount * exchangeRate;

                // Exibindo o resultado da conversão
                System.out.println(f.format(amount) + " " + fromCode + " = " + f.format(convertedAmount) + " " + toCode);
            } else {
                // Caso a solicitação HTTP falhe
                System.out.println("A solicitação de requisição falhou! Código de resposta: " + responseCode);
            }
        } catch (MalformedURLException e) {
            // Lidando com URL mal formada
            System.out.println("URL mal formada: " + e.getMessage());
        } catch (IOException e) {
            // Lidando com erros de conexão à API
            System.out.println("Erro ao conectar à API: " + e.getMessage());
        } catch (JSONException e) {
            // Lidando com erros ao analisar a resposta JSON
            System.out.println("Erro ao analisar a resposta JSON: " + e.getMessage());
        }
    }
}
