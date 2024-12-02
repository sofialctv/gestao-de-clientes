//package utils;
//
//public class OrdenarCliente {
//}
//
//]
//        package services;
//
//import interfaces.ArquivoSequencial;
//import interfaces.Buffer;
//import models.Cliente;
//
//import java.io.*;
//        import java.util.*;
//
//public class MultiWayMergeSort {
//
//    private static final int TamanhoBloco = 100; // Define o tamanho de cada bloco de clientes a ser processado
//
//    // Método para ordenar o arquivo
//    public void ordenarArquivo(String nomeArquivoEntrada, String nomeArquivoSaida, ArquivoSequencial<Cliente> arquivoSequencial, Buffer<Cliente> buffer) throws IOException, ClassNotFoundException {
//        // Dividir em blocos e ordenar os blocos
//        List<String> arquivosTemporarios = dividirEOrdenarEmBlocos(nomeArquivoEntrada, arquivoSequencial, buffer);
//
//        // Mesclar os blocos ordenados
//        mesclarArquivos(arquivosTemporarios, nomeArquivoSaida, arquivoSequencial, buffer);
//
//        // Limpeza dos arquivos temporários
//        for (String arquivoTemp : arquivosTemporarios) {
//            new File(arquivoTemp).delete(); // Deleta arquivos temporários após o merge
//        }
//    }
//
//    // Método para dividir o arquivo em blocos e ordená-los
//    private List<String> dividirEOrdenarEmBlocos(String nomeArquivoEntrada, ArquivoSequencial<Cliente> arquivoSequencial, Buffer<Cliente> buffer) throws IOException, ClassNotFoundException {
//        List<String> arquivosTemporarios = new ArrayList<>();
//        arquivoSequencial.abrirArquivo(nomeArquivoEntrada, "r", Cliente.class);
//        int contadorBloco = 0;
//
//        while (true) {
//            List<Cliente> clientes = arquivoSequencial.leiaDoArquivo(TamanhoBloco);
//            if (clientes.isEmpty()) break; // Não há mais registros para ler
//
//            // Ordena os clientes por nome
//            clientes.sort(Comparator.comparing(Cliente::getNome));
//
//            // Cria um arquivo temporário para o bloco ordenado
//            String nomeArquivoTemp = "temp_bloco_" + contadorBloco + ".dat";
//            arquivosTemporarios.add(nomeArquivoTemp);
//
//            // Grava o bloco ordenado no arquivo temporário
//            ArquivoSequencial<Cliente> tempArquivo = new ArquivoSequencialImpl<>();
//            tempArquivo.abrirArquivo(nomeArquivoTemp, "rw", Cliente.class);
//            tempArquivo.escreveNoArquivo(clientes);
//            tempArquivo.fechaArquivo();
//
//            contadorBloco++;
//        }
//
//        arquivoSequencial.fechaArquivo();
//        return arquivosTemporarios;
//    }
//
//    // Método para mesclar os arquivos temporários
//    private void mesclarArquivos(List<String> arquivosTemporarios, String nomeArquivoSaida, ArquivoSequencial<Cliente> arquivoSequencial, Buffer<Cliente> buffer) throws IOException, ClassNotFoundException {
//        PriorityQueue<Cliente> pq = new PriorityQueue<>(Comparator.comparing(Cliente::getNome));
//
//        // Abre todos os arquivos temporários para leitura
//        List<ArquivoSequencial<Cliente>> arquivos = new ArrayList<>();
//        for (String arquivo : arquivosTemporarios) {
//            ArquivoSequencial<Cliente> tempArquivo = new ArquivoSequencialImpl<>();
//            tempArquivo.abrirArquivo(arquivo, "r", Cliente.class);
//            arquivos.add(tempArquivo);
//
//            // Carrega o primeiro cliente de cada arquivo para a fila de prioridade
//            List<Cliente> clientesTemp = tempArquivo.leiaDoArquivo(1);
//            if (!clientesTemp.isEmpty()) {
//                pq.add(clientesTemp.get(0));
//            }
//        }
//
//        // Cria o arquivo de saída para os dados ordenados
//        ArquivoSequencial<Cliente> arquivoSaida = new ArquivoSequencialImpl<>();
//        arquivoSaida.abrirArquivo(nomeArquivoSaida, "rw", Cliente.class);
//
//        // Mescla os dados utilizando a fila de prioridade
//        while (!pq.isEmpty()) {
//            Cliente cliente = pq.poll();
//            arquivoSaida.escreveNoArquivo(Collections.singletonList(cliente));
//
//            // Carrega o próximo cliente do arquivo correspondente
//            for (int i = 0; i < arquivos.size(); i++) {
//                ArquivoSequencial<Cliente> tempArquivo = arquivos.get(i);
//                List<Cliente> clientesTemp = tempArquivo.leiaDoArquivo(1);
//                if (!clientesTemp.isEmpty() && clientesTemp.get(0).getNome().equals(cliente.getNome())) {
//                    pq.add(clientesTemp.get(0));
//                    break;
//                }
//            }
//        }
//
//        // Fecha os arquivos
//        for (ArquivoSequencial<Cliente> tempArquivo : arquivos) {
//            tempArquivo.fechaArquivo();
//        }
//
//        arquivoSaida.fechaArquivo();
//    }
//}
