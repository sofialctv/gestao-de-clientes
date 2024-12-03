package utils;

import java.io.*;
import java.util.*;

public class ExternalSort {

    // Tamanho máximo de memória para uso (ajuste conforme necessário)
    private static final long MAX_MEMORY = Runtime.getRuntime().freeMemory() / 2;

    /**
     * Ordena um arquivo binário usando External Merge Sort.
     * @param inputFile O arquivo binário de entrada.
     * @param outputFile O arquivo binário de saída ordenado.
     * @throws IOException Se ocorrer um erro de E/S.
     */
    public static void sort(File inputFile, File outputFile) throws IOException {
        List<File> tempFiles = splitAndSort(inputFile);
        mergeSortedFiles(tempFiles, outputFile);
    }

    /**
     * Divide o arquivo binário em blocos menores, ordena cada bloco e salva em arquivos temporários.
     * @param inputFile O arquivo binário de entrada.
     * @return Lista de arquivos temporários ordenados.
     * @throws IOException Se ocorrer um erro de E/S.
     */
    private static List<File> splitAndSort(File inputFile) throws IOException {
        List<File> tempFiles = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)))) {
            List<byte[]> buffer = new ArrayList<>();
            long currentBlockSize = 0;
            int recordSize = getRecordSize(); // Método para obter o tamanho de cada registro
            byte[] record;
            while (dis.available() > 0) {
                // Lê registros até que o bloco atual atinja o tamanho máximo permitido
                while (currentBlockSize < MAX_MEMORY && dis.available() > 0) {
                    record = new byte[recordSize];
                    dis.readFully(record);
                    buffer.add(record);
                    currentBlockSize += record.length;
                }
                // Ordena o bloco atual
                buffer.sort(new RecordComparator());
                // Escreve o bloco ordenado em um arquivo temporário
                File tempFile = File.createTempFile("sortInBatch", ".tmp");
                tempFile.deleteOnExit();
                try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)))) {
                    for (byte[] b : buffer) {
                        dos.write(b);
                    }
                }
                tempFiles.add(tempFile);
                buffer.clear();
                currentBlockSize = 0;
            }
        }
        return tempFiles;
    }

    /**
     * Mescla os arquivos temporários ordenados em um único arquivo de saída.
     * @param files Lista de arquivos temporários ordenados.
     * @param outputFile O arquivo de saída final.
     * @throws IOException Se ocorrer um erro de E/S.
     */
    private static void mergeSortedFiles(List<File> files, File outputFile) throws IOException {
        PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<>(new Comparator<BinaryFileBuffer>() {
            public int compare(BinaryFileBuffer b1, BinaryFileBuffer b2) {
                return recordComparator.compare(b1.peek(), b2.peek());
            }
        });
        for (File file : files) {
            BinaryFileBuffer bfb = new BinaryFileBuffer(file);
            if (!bfb.empty()) {
                pq.add(bfb);
            }
        }
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
            while (!pq.isEmpty()) {
                BinaryFileBuffer bfb = pq.poll();
                byte[] record = bfb.pop();
                dos.write(record);
                if (bfb.empty()) {
                    bfb.close();
                    bfb.originalFile.delete();
                } else {
                    pq.add(bfb);
                }
            }
        }
    }

    // Comparador para registros binários
    private static final RecordComparator recordComparator = new RecordComparator();

    /**
     * Classe auxiliar que mantém um buffer para um arquivo binário temporário.
     */
    static class BinaryFileBuffer {
        public DataInputStream dis;
        public File originalFile;
        private byte[] cache;
        private boolean empty;
        private int recordSize;

        public BinaryFileBuffer(File file) throws IOException {
            originalFile = file;
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            recordSize = getRecordSize();
            reload();
        }

        public boolean empty() {
            return empty;
        }

        private void reload() throws IOException {
            if (dis.available() <= 0) {
                empty = true;
                cache = null;
            } else {
                cache = new byte[recordSize];
                dis.readFully(cache);
                empty = false;
            }
        }

        public void close() throws IOException {
            dis.close();
        }

        public byte[] peek() {
            if (empty()) return null;
            return cache;
        }

        public byte[] pop() throws IOException {
            byte[] answer = peek();
            reload();
            return answer;
        }
    }

    /**
     * Comparador para registros binários.
     * Ajuste este comparador de acordo com a estrutura dos registros.
     */
    static class RecordComparator implements Comparator<byte[]> {
        @Override
        public int compare(byte[] o1, byte[] o2) {
            // Exemplo: comparar inteiros de 4 bytes no início de cada registro
            int int1 = bytesToInt(o1, 0);
            int int2 = bytesToInt(o2, 0);
            return Integer.compare(int1, int2);
        }
    }

    // Métodos auxiliares

    /**
     * Obtém o tamanho de cada registro em bytes.
     * Ajuste este método de acordo com a estrutura dos registros.
     */
    private static int getRecordSize() {
        // Exemplo: registros de 100 bytes
        return 100;
    }

    /**
     * Converte um array de bytes em um inteiro.
     * @param bytes O array de bytes.
     * @param offset O deslocamento inicial.
     * @return O inteiro convertido.
     */
    private static int bytesToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24)
                | ((bytes[offset + 1] & 0xFF) << 16)
                | ((bytes[offset + 2] & 0xFF) << 8)
                | (bytes[offset + 3] & 0xFF);
    }

    public static void main(String[] args) {
        // Exemplo de uso:
        File inputFile = new File("clientes.bin");
        File outputFile = new File("saida_ordenada.bin");
        try {
            sort(inputFile, outputFile);
            System.out.println("Ordenação concluída. Arquivo de saída: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
