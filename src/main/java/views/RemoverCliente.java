package views;

import models.Cliente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RemoverCliente {

    // Faz leitura do arquivo em lotes
    public static List<Cliente> leClientes(String nomeArquivo, int tamanhoBuffer) throws IOException, ClassNotFoundException {

        List<Cliente> buffer = new ArrayList<>();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            while (true) {
                try {
                    Cliente cliente = (Cliente) inputStream.readObject();
                    buffer.add(cliente);

                    if (buffer.size() == tamanhoBuffer) {
                        return buffer;
                    }
                } catch (EOFException e) {
                    break;
                }
            }
        }
        return buffer;
    }

    public static void escreveClientes(String nomeArquivo, List<Cliente> clientes) throws IOException {

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {

            for (Cliente cliente : clientes) {
                outputStream.writeObject(cliente);
            }
        }
    }

    // Realiza a exclusão do cliente e grava o arquivo
    public static void execute(String arquivoDeEntrada, String arquivoDeSaida, String clienteRemover, int tamanhoBuffer) {
        List<Cliente> buffer = new ArrayList<>();
        try {
            boolean clienteEncontrado = false;
            
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(arquivoDeEntrada));
                 ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(arquivoDeSaida))) {

                while (true) {
                    try {

                        // Leitura em lotes dos clientes
                        Cliente cliente = (Cliente) inputStream.readObject();
                        if (!cliente.getNome().equals(clienteRemover)) {
                            buffer.add(cliente);
                        } else {
                            clienteEncontrado = true;
                        }

                        if (buffer.size() == tamanhoBuffer) {
                            for (Cliente c : buffer) {
                                outputStream.writeObject(c);
                            }
                            buffer.clear();
                        }
                    } catch (EOFException e) {
                        break;
                    }
                }

                // Gravar os demais clientes no buffer
                for (Cliente c : buffer) {
                    outputStream.writeObject(c);
                }
            }

            if (clienteEncontrado) {
                System.out.println("Cliente removido com sucesso.");
            } else {
                System.out.println("Cliente não encontrado.");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}