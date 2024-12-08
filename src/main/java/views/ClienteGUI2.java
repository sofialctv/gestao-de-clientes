package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.ArquivoCliente;
import models.BufferDeClientes;
import models.Cliente;
import utils.OrdenarCliente;
import utils.GeradorDeArquivosDeClientes;  // Importando o GeradorDeArquivosDeClientes

public class ClienteGUI2 extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private final BufferDeClientes bufferDeClientes;
    private final int TAMANHO_BUFFER = 10000;
    private int registrosCarregados = 0; // Contador de registros já carregados
    private String arquivoSelecionado;
    private boolean arquivoCarregado = false; // Para verificar se o arquivo foi carregado
    private final List<Cliente> listaClientes;
    private JTextField campoPesquisa; // Campo de pesquisa para "Pesquisar Cliente"

    public ClienteGUI2() {
        setTitle("Gerenciamento de Clientes");
        setSize(800, 600); // Reduzido o tamanho da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        bufferDeClientes = new BufferDeClientes();
        listaClientes = new ArrayList<>();
        criarInterface();
    }

    private void carregarArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        int retorno = fileChooser.showOpenDialog(this);

        if (retorno == JFileChooser.APPROVE_OPTION) {
            arquivoSelecionado = fileChooser.getSelectedFile().getAbsolutePath();
            bufferDeClientes.associaBuffer(new ArquivoCliente()); // Substitua por sua implementação
            bufferDeClientes.inicializaBuffer("leitura", arquivoSelecionado); // Passa o nome do arquivo aqui
            registrosCarregados = 0; // Reseta o contador
            tableModel.setRowCount(0); // Limpa a tabela
            carregarMaisClientes(); // Carrega os primeiros clientes
            arquivoCarregado = true; // Marca que o arquivo foi carregado
        }
    }

    private void criarInterface() {
        JPanel panel = new JPanel(new BorderLayout());

        // Painel de botões na parte inferior
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Botão para carregar clientes
        JButton btnCarregarCliente = new JButton("Carregar Clientes");
        JButton btnInserirCliente = new JButton("Inserir Cliente");
        JButton btnRemoverCliente = new JButton("Remover Cliente");
        JButton btnOrdenarClientes = new JButton("Ordenar Clientes");
        JButton btnGerarArquivoClientes = new JButton("Gerar Arquivo de Clientes");  // Novo botão

        // Inicialmente, só o botão de carregar é visível
        btnInserirCliente.setVisible(false);
        btnRemoverCliente.setVisible(false);
        btnOrdenarClientes.setVisible(false);
        btnGerarArquivoClientes.setVisible(true);

        // Tabela para mostrar os clientes
        tableModel = new DefaultTableModel(new String[]{"#", "Nome", "Sobrenome", "Telefone", "Endereço", "Credit Score"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Ação para carregar clientes
        btnCarregarCliente.addActionListener(e -> {
            carregarArquivo();
            btnInserirCliente.setVisible(true);
            btnRemoverCliente.setVisible(true);
            btnOrdenarClientes.setVisible(true);
        });

        // Ação para inserir cliente
        btnInserirCliente.addActionListener(e -> new InserirCliente(listaClientes, tableModel, bufferDeClientes));

        // Ação para remover cliente
        btnRemoverCliente.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cliente para remover.");
            }
        });

        // Ação para ordenar clientes
        btnOrdenarClientes.addActionListener(e -> ordenarAlfabeticamente());

        // Ação para gerar o arquivo de clientes
        btnGerarArquivoClientes.addActionListener(e -> gerarArquivoClientes());

        // Adicionando botões ao painel
        btnPanel.add(btnGerarArquivoClientes);
        btnPanel.add(btnCarregarCliente);
        btnPanel.add(btnInserirCliente);
        btnPanel.add(btnRemoverCliente);
        btnPanel.add(btnOrdenarClientes);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void carregarMaisClientes() {
        Cliente[] clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER);
        if (clientes != null && clientes.length > 0) {
            for (Cliente cliente : clientes) {
                if (cliente != null) {
                    tableModel.addRow(new Object[]{
                            tableModel.getRowCount() + 1,
                            cliente.getNome(),
                            cliente.getSobrenome(),
                            cliente.getTelefone(),
                            cliente.getEndereco(),
                            cliente.getCreditScore()});
                }
            }
            registrosCarregados += clientes.length;
        }
    }

    private void ordenarAlfabeticamente() {
        if (!arquivoCarregado) {
            JOptionPane.showMessageDialog(this, "Nenhum arquivo foi carregado.");
            return;
        }

        String outputFile = arquivoSelecionado + "_ordenado.dat";

        try {
            OrdenarCliente ordenarCliente = new OrdenarCliente();
            ordenarCliente.sort(arquivoSelecionado, outputFile);

            JOptionPane.showMessageDialog(this, "Clientes ordenados com sucesso!");
            tableModel.setRowCount(0);

            bufferDeClientes.inicializaBuffer("leitura", outputFile);
            carregarMaisClientes();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao ordenar clientes: " + e.getMessage());
        }
    }

    // Método para gerar o arquivo de clientes fictícios usando a classe GeradorDeArquivosDeClientes
    private void gerarArquivoClientes() {
        // Solicita o nome do arquivo ao usuário
        String nomeArquivo = JOptionPane.showInputDialog(this, "Digite o nome do arquivo de saída:");

        // Verifica se o usuário forneceu um nome válido
        if (nomeArquivo == null || nomeArquivo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome de arquivo inválido. Por favor, insira um nome válido.");
            return;
        }

        // Solicita a quantidade de clientes ao usuário
        String inputQuantidade = JOptionPane.showInputDialog(this, "Digite a quantidade de clientes a ser gerada:");
        int quantidadeClientes;

        // Valida a quantidade de clientes
        try {
            quantidadeClientes = Integer.parseInt(inputQuantidade);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Entrada inválida. Por favor, insira um número.");
            return;
        }

        // Instancia o gerador de arquivos de clientes
        GeradorDeArquivosDeClientes gerador = new GeradorDeArquivosDeClientes();

        // Gera o arquivo de clientes com base no nome e quantidade fornecida
        gerador.geraGrandeDataSetDeClientes(nomeArquivo, quantidadeClientes);

        // Notifica o usuário que o arquivo foi gerado com sucesso
        JOptionPane.showMessageDialog(this, "Arquivo de clientes gerado com sucesso!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteGUI2 gui = new ClienteGUI2();
            gui.setVisible(true);
        });
    }
}