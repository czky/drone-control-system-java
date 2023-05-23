import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DroneFleetManagementSystem extends JFrame {
    private JTextField modelTextField;
    private JTextField flightRangeTextField;
    private JTextField typeTextField;
    private JTextArea recordsTextArea;
    private List<Drone> droneList;

    public DroneFleetManagementSystem() {
        droneList = new ArrayList<>();

        setTitle("Drone Fleet Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        modelTextField = new JTextField();
        flightRangeTextField = new JTextField();
        typeTextField = new JTextField();
        inputPanel.add(new JLabel("Drone Model:"));
        inputPanel.add(modelTextField);
        inputPanel.add(new JLabel("Max Flight Range:"));
        inputPanel.add(flightRangeTextField);
        inputPanel.add(new JLabel("Drone Type:"));
        inputPanel.add(typeTextField);

        JButton addButton = new JButton("Add Drone");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String model = modelTextField.getText();
                int flightRange = Integer.parseInt(flightRangeTextField.getText());
                String type = typeTextField.getText();

                Drone newDrone = new Drone(model, flightRange, type) {
                    @Override
                    public void displayInfo() {
                        displayDrone(this);
                    }
                };
                droneList.add(newDrone);
                recordsTextArea.append("Added: " + newDrone.getModel() + "\n");

                clearInputFields();
            }
        });

        JButton searchButton = new JButton("Search by Model");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String model = modelTextField.getText();
                StringBuilder message = new StringBuilder();
                boolean droneFound = false;

                for (Drone drone : droneList) {
                    if (drone.getModel().equals(model)) {
                        message.append("Model: ").append(drone.getModel()).append(", Range: ")
                                .append(drone.getMaxFlightRange()).append(", Type: ").append(drone.getType()).append("\n");
                        droneFound = true;
                    }
                }

                if (droneFound) {
                    displayMessage("Search Results:\n" + message.toString());
                } else {
                    displayMessage("Drone not found.");
                }
            }
        });


        JButton displayAllButton = new JButton("Display All Drones");
        displayAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Drone drone : droneList) {
                    displayDrone(drone);
                }
            }
        });

        JButton saveButton = new JButton("Save to JSON");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = chooseFileForSave();
                if (file != null) {
                    saveToJson(file);
                    recordsTextArea.append("Saved to file: " + file.getAbsolutePath() + "\n");
                }
            }
        });

        JButton loadButton = new JButton("Load from JSON");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = chooseFileForLoad();
                if (file != null) {
                    loadFromJson(file);
                    recordsTextArea.append("Loaded from file: " + file.getAbsolutePath() + "\n");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3));
        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(displayAllButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        recordsTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(recordsTextArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private void clearInputFields() {
        modelTextField.setText("");
        flightRangeTextField.setText("");
        typeTextField.setText("");
    }

    private void displayDrone(Drone drone) {
        recordsTextArea.append("Model: " + drone.getModel() + ", Range: " +
                drone.getMaxFlightRange() + ", Type: " + drone.getType() + "\n");
    }

    private File chooseFileForSave() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(DroneFleetManagementSystem.this);
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private File chooseFileForLoad() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(DroneFleetManagementSystem.this);
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private void saveToJson(File file) {
        try (FileWriter writer = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(writer)) {
            for (Drone drone : droneList) {
                bw.write(drone.getModel() + "," + drone.getMaxFlightRange() + "," + drone.getType());
                bw.newLine();
            }
        } catch (IOException e) {
            displayMessage("Error saving drone records to file: " + e.getMessage());
        }
    }

    private void loadFromJson(File file) {
        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 3) {
                    String model = fields[0];
                    int flightRange = Integer.parseInt(fields[1]);
                    String type = fields[2];
                    Drone drone = new Drone(model, flightRange, type) {
                        @Override
                        public void displayInfo() {
                            displayDrone(this);
                        }
                    };
                    droneList.add(drone);
                }
            }
        } catch (IOException e) {
            displayMessage("Error loading drone records from file: " + e.getMessage());
        }
    }

    private void saveDroneRecordsToFile() {
        String filePath = chooseFilePath("Save Drone Records");
        if (filePath != null) {
            try (FileOutputStream fos = new FileOutputStream(filePath);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(droneList);
                displayMessage("Drone records saved to file: " + filePath);
            } catch (IOException e) {
                displayMessage("Error saving drone records to file: " + e.getMessage());
            }
        }
    }

    private void loadDroneRecordsFromFile() {
        String filePath = chooseFilePath("Load Drone Records");
        if (filePath != null) {
            try (FileInputStream fis = new FileInputStream(filePath);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                droneList = (List<Drone>) ois.readObject();
                displayMessage("Drone records loaded from file: " + filePath);
            } catch (IOException | ClassNotFoundException e) {
                displayMessage("Error loading drone records from file: " + e.getMessage());
            }
        }
    }

    private void displayMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    private String chooseFilePath(String dialogTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        int userChoice = fileChooser.showDialog(this, dialogTitle);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

}
