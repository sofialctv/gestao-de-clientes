package views;

import models.BufferDeClientes;
import models.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class InserirCliente {

    private List<Cliente> listaClientes;
    private DefaultTableModel tableModel;
    private BufferDeClientes bufferDeClientes;
    private String arquivoSelecionado;

    public InserirCliente(List<Cliente> listaClientes, DefaultTableModel tableModel, BufferDeClientes bufferDeClientes, String arquivoSelecionado) {
        this.listaClientes = listaClientes;
        this.tableModel = tableModel;
        this.bufferDeClientes = bufferDeClientes;
        this.arquivoSelecionado = arquivoSelecionado;

        // Inicializar a inserção do cliente
        inserirNovoCliente();
    }

    private void inserirNovoCliente() {
        // Criação dos campos para os dados do novo cliente
        JTextField nomeField = new JTextField();
        JTextField sobrenomeField = new JTextField();
        JTextField telefoneField = new JTextField();
        JTextField enderecoField = new JTextField();
        JTextField creditScoreField = new JTextField();

        Object[] message = {
                "Nome:", nomeField,
                "Sobrenome:", sobrenomeField,
                "Telefone:", telefoneField,
                "Endereço:", enderecoField,
                "Credit Score:", creditScoreField
        };

        // Mostra o diálogo para inserção dos dados
        int option = JOptionPane.showConfirmDialog(null, message, "Inserir Novo Cliente", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Criação do novo cliente com os dados inseridos
                Cliente novoCliente = new Cliente(
                        nomeField.getText(),
                        sobrenomeField.getText(),
                        telefoneField.getText(),
                        enderecoField.getText(),
                        Integer.parseInt(creditScoreField.getText())
                );

                // Adiciona o novo cliente à lista de clientes
                listaClientes.add(novoCliente);

                // Adiciona o novo cliente na tabela visual
                tableModel.addRow(new Object[]{
                        tableModel.getRowCount() + 1, novoCliente.getNome(), novoCliente.getSobrenome(),
                        novoCliente.getTelefone(), novoCliente.getEndereco(), novoCliente.getCreditScore()
                });

                // Chama o método para escrever no arquivo usando append
                escreverClienteNoArquivo(novoCliente);

                JOptionPane.showMessageDialog(null, "Cliente inserido com sucesso!");

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Erro: Credit Score deve ser um número.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao inserir cliente: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Método que escreve o cliente no arquivo sem sobrescrever os dados já existentes
    private void escreverClienteNoArquivo(Cliente novoCliente) {
        try {
            File arquivo = new File(arquivoSelecionado);
            boolean arquivoExiste = arquivo.exists();

            // Fluxo para escrita no arquivo, utilizando append
            try (FileOutputStream fos = new FileOutputStream(arquivo, true);
                 ObjectOutputStream oos = arquivoExiste
                         ? new AppendableObjectOutputStream(fos)
                         : new ObjectOutputStream(fos)) {

                oos.writeObject(novoCliente);
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao gravar o cliente no arquivo: " + e.getMessage());
        }
    }

    // Classe personalizada para evitar a reescrita do cabeçalho do ObjectOutputStream
    static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(FileOutputStream fos) throws IOException {
            super(fos);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset();  // Evita a reescrita do cabeçalho
        }
    }
}