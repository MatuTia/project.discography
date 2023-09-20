package project.discography.view.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import project.discography.controller.DiscographyController;
import project.discography.model.Album;
import project.discography.model.Musician;
import project.discography.view.DiscographyView;

public class DiscographySwingView extends JFrame implements DiscographyView {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private transient DiscographyController controller;

	private JTextField textFieldIdMusician;
	private JTextField textFieldNameMusician;
	private JTextField textFieldIdAlbum;
	private JTextField textFieldTitleAlbum;
	private JList<Musician> listMusicians;
	private JList<Album> listAlbums;
	private JButton btnAddMusician;
	private JButton btnDeleteMusician;
	private JButton btnUpdateMusician;
	private JButton btnAddAlbum;
	private JButton btnDeleteAlbum;
	private JButton btnUpdateAlbum;
	private JLabel labelError;

	private DefaultListModel<Musician> musicianListModel;

	private DefaultListModel<Album> albumListModel;

	DefaultListModel<Musician> getMusicianListModel() {
		return musicianListModel;
	}

	DefaultListModel<Album> getAlbumListModel() {
		return albumListModel;
	}

	public void setController(DiscographyController controller) {
		this.controller = controller;
	}

	public DiscographySwingView() {
		setTitle("Discography View");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 520, 560);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JLabel lblIdMusician = new JLabel("id Musician");
		GridBagConstraints gbc_lblIdMusician = new GridBagConstraints();
		gbc_lblIdMusician.anchor = GridBagConstraints.EAST;
		gbc_lblIdMusician.insets = new Insets(0, 0, 5, 5);
		gbc_lblIdMusician.gridx = 0;
		gbc_lblIdMusician.gridy = 0;
		contentPane.add(lblIdMusician, gbc_lblIdMusician);

		textFieldIdMusician = new JTextField();
		textFieldIdMusician.setName("idMusician");
		GridBagConstraints gbc_textFieldIdMusician = new GridBagConstraints();
		gbc_textFieldIdMusician.gridwidth = 2;
		gbc_textFieldIdMusician.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldIdMusician.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldIdMusician.gridx = 1;
		gbc_textFieldIdMusician.gridy = 0;
		contentPane.add(textFieldIdMusician, gbc_textFieldIdMusician);
		textFieldIdMusician.setColumns(10);

		textFieldIdMusician.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				btnAddMusicianEnabler();
				super.keyReleased(e);
			}

		});

		JLabel lblNameMusician = new JLabel("name Musician");
		GridBagConstraints gbc_lblNameMusician = new GridBagConstraints();
		gbc_lblNameMusician.anchor = GridBagConstraints.EAST;
		gbc_lblNameMusician.insets = new Insets(0, 0, 5, 5);
		gbc_lblNameMusician.gridx = 0;
		gbc_lblNameMusician.gridy = 1;
		contentPane.add(lblNameMusician, gbc_lblNameMusician);

		textFieldNameMusician = new JTextField();
		textFieldNameMusician.setName("nameMusician");
		GridBagConstraints gbc_textFieldNameMusician = new GridBagConstraints();
		gbc_textFieldNameMusician.gridwidth = 2;
		gbc_textFieldNameMusician.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldNameMusician.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldNameMusician.gridx = 1;
		gbc_textFieldNameMusician.gridy = 1;
		contentPane.add(textFieldNameMusician, gbc_textFieldNameMusician);
		textFieldNameMusician.setColumns(10);

		textFieldNameMusician.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				btnAddMusicianEnabler();
				btnUpdateMusicianEnabler();
				super.keyReleased(e);
			}

		});

		JScrollPane scrollPaneMusician = new JScrollPane();
		GridBagConstraints gbc_scrollPaneMusician = new GridBagConstraints();
		gbc_scrollPaneMusician.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPaneMusician.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneMusician.gridwidth = 3;
		gbc_scrollPaneMusician.gridx = 0;
		gbc_scrollPaneMusician.gridy = 2;
		contentPane.add(scrollPaneMusician, gbc_scrollPaneMusician);

		musicianListModel = new DefaultListModel<>();

		listMusicians = new JList<>(getMusicianListModel());
		listMusicians.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneMusician.setViewportView(listMusicians);
		listMusicians.setName("musicians");

		listMusicians.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && listMusicians.getSelectedIndex() != -1) {
				controller.musicianDiscography(listMusicians.getSelectedValue());
			}
			btnDeleteMusicianEnabler();
			btnUpdateMusicianEnabler();
			btnAddAlbumEnabler();
			btnDeleteAlbumEnabler();
			btnUpdateAlbumEnabler();
		});

		listMusicians.setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Musician musician = (Musician) value;
				return super.getListCellRendererComponent(list, displayMusician(musician), index, isSelected,
						cellHasFocus);
			}

		});

		btnAddMusician = new JButton("Add Musician");
		btnAddMusician.setEnabled(false);
		GridBagConstraints gbc_btnAddMusician = new GridBagConstraints();
		gbc_btnAddMusician.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddMusician.gridx = 0;
		gbc_btnAddMusician.gridy = 3;
		contentPane.add(btnAddMusician, gbc_btnAddMusician);

		btnAddMusician.addActionListener(e -> controller
				.newMusician(new Musician(textFieldIdMusician.getText(), textFieldNameMusician.getText())));

		btnDeleteMusician = new JButton("Delete Musician");
		btnDeleteMusician.setEnabled(false);
		GridBagConstraints gbc_btnDeleteMusician = new GridBagConstraints();
		gbc_btnDeleteMusician.insets = new Insets(0, 0, 5, 5);
		gbc_btnDeleteMusician.gridx = 1;
		gbc_btnDeleteMusician.gridy = 3;
		contentPane.add(btnDeleteMusician, gbc_btnDeleteMusician);

		btnDeleteMusician.addActionListener(e -> controller.deleteMusician(listMusicians.getSelectedValue()));

		btnUpdateMusician = new JButton("Update Musician");
		btnUpdateMusician.setEnabled(false);
		GridBagConstraints gbc_btnUpdateMusician = new GridBagConstraints();
		gbc_btnUpdateMusician.insets = new Insets(0, 0, 5, 0);
		gbc_btnUpdateMusician.gridx = 2;
		gbc_btnUpdateMusician.gridy = 3;
		contentPane.add(btnUpdateMusician, gbc_btnUpdateMusician);

		btnUpdateMusician.addActionListener(e -> {
			Musician toUpdate = listMusicians.getSelectedValue();
			controller.updateMusician(toUpdate, new Musician(toUpdate.getId(), textFieldNameMusician.getText()));
		});

		JLabel lblIdAlbum = new JLabel("id Album");
		GridBagConstraints gbc_lblIdAlbum = new GridBagConstraints();
		gbc_lblIdAlbum.insets = new Insets(0, 0, 5, 5);
		gbc_lblIdAlbum.anchor = GridBagConstraints.EAST;
		gbc_lblIdAlbum.gridx = 0;
		gbc_lblIdAlbum.gridy = 4;
		contentPane.add(lblIdAlbum, gbc_lblIdAlbum);

		textFieldIdAlbum = new JTextField();
		textFieldIdAlbum.setName("idAlbum");
		GridBagConstraints gbc_textFieldIdAlbum = new GridBagConstraints();
		gbc_textFieldIdAlbum.gridwidth = 2;
		gbc_textFieldIdAlbum.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldIdAlbum.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldIdAlbum.gridx = 1;
		gbc_textFieldIdAlbum.gridy = 4;
		contentPane.add(textFieldIdAlbum, gbc_textFieldIdAlbum);
		textFieldIdAlbum.setColumns(10);

		textFieldIdAlbum.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				btnAddAlbumEnabler();
				super.keyReleased(e);
			}

		});

		JLabel lblTitleAlbum = new JLabel("title Album");
		GridBagConstraints gbc_lblTitleAlbum = new GridBagConstraints();
		gbc_lblTitleAlbum.insets = new Insets(0, 0, 5, 5);
		gbc_lblTitleAlbum.anchor = GridBagConstraints.EAST;
		gbc_lblTitleAlbum.gridx = 0;
		gbc_lblTitleAlbum.gridy = 5;
		contentPane.add(lblTitleAlbum, gbc_lblTitleAlbum);

		textFieldTitleAlbum = new JTextField();
		textFieldTitleAlbum.setName("titleAlbum");
		GridBagConstraints gbc_textFieldTitleAlbum = new GridBagConstraints();
		gbc_textFieldTitleAlbum.gridwidth = 2;
		gbc_textFieldTitleAlbum.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldTitleAlbum.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldTitleAlbum.gridx = 1;
		gbc_textFieldTitleAlbum.gridy = 5;
		contentPane.add(textFieldTitleAlbum, gbc_textFieldTitleAlbum);
		textFieldTitleAlbum.setColumns(10);

		textFieldTitleAlbum.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				btnAddAlbumEnabler();
				btnUpdateAlbumEnabler();
				super.keyReleased(e);
			}

		});

		JScrollPane scrollPaneAlbum = new JScrollPane();
		GridBagConstraints gbc_scrollPaneAlbum = new GridBagConstraints();
		gbc_scrollPaneAlbum.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPaneAlbum.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneAlbum.gridwidth = 3;
		gbc_scrollPaneAlbum.gridx = 0;
		gbc_scrollPaneAlbum.gridy = 6;
		contentPane.add(scrollPaneAlbum, gbc_scrollPaneAlbum);

		albumListModel = new DefaultListModel<>();

		listAlbums = new JList<>(getAlbumListModel());
		listAlbums.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneAlbum.setViewportView(listAlbums);
		listAlbums.setName("albums");

		listAlbums.addListSelectionListener(e -> {
			btnDeleteAlbumEnabler();
			btnUpdateAlbumEnabler();
		});

		btnAddAlbum = new JButton("Add Album");
		btnAddAlbum.setEnabled(false);
		GridBagConstraints gbc_btnAddAlbum = new GridBagConstraints();
		gbc_btnAddAlbum.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddAlbum.gridx = 0;
		gbc_btnAddAlbum.gridy = 7;
		contentPane.add(btnAddAlbum, gbc_btnAddAlbum);

		btnAddAlbum.addActionListener(e -> {
			Musician musician = listMusicians.getSelectedValue();
			controller.newAlbum(musician,
					new Album(textFieldIdAlbum.getText(), textFieldTitleAlbum.getText(), musician.getId()));
		});

		btnDeleteAlbum = new JButton("Delete Album");
		btnDeleteAlbum.setEnabled(false);
		GridBagConstraints gbc_btnDeleteAlbum = new GridBagConstraints();
		gbc_btnDeleteAlbum.insets = new Insets(0, 0, 5, 5);
		gbc_btnDeleteAlbum.gridx = 1;
		gbc_btnDeleteAlbum.gridy = 7;
		contentPane.add(btnDeleteAlbum, gbc_btnDeleteAlbum);

		btnDeleteAlbum.addActionListener(
				e -> controller.deleteAlbum(listMusicians.getSelectedValue(), listAlbums.getSelectedValue()));

		btnUpdateAlbum = new JButton("Update Album");
		btnUpdateAlbum.setEnabled(false);
		GridBagConstraints gbc_btnUpdateAlbum = new GridBagConstraints();
		gbc_btnUpdateAlbum.insets = new Insets(0, 0, 5, 0);
		gbc_btnUpdateAlbum.gridx = 2;
		gbc_btnUpdateAlbum.gridy = 7;
		contentPane.add(btnUpdateAlbum, gbc_btnUpdateAlbum);

		btnUpdateAlbum.addActionListener(e -> {
			Album album = listAlbums.getSelectedValue();
			controller.updateAlbum(listMusicians.getSelectedValue(), album,
					new Album(album.getId(), textFieldTitleAlbum.getText(), album.getMusician()));
		});

		labelError = new JLabel(" ");
		labelError.setForeground(Color.RED);
		labelError.setName("error");
		GridBagConstraints gbc_labelError = new GridBagConstraints();
		gbc_labelError.gridwidth = 3;
		gbc_labelError.insets = new Insets(0, 0, 0, 5);
		gbc_labelError.gridx = 0;
		gbc_labelError.gridy = 8;
		contentPane.add(labelError, gbc_labelError);
	}

	private void btnAddMusicianEnabler() {
		btnAddMusician.setEnabled(
				!textFieldIdMusician.getText().trim().isEmpty() && !textFieldNameMusician.getText().trim().isEmpty());
	}

	private void btnDeleteMusicianEnabler() {
		btnDeleteMusician.setEnabled(listMusicians.getSelectedIndex() != -1);
	}

	private void btnUpdateMusicianEnabler() {
		btnUpdateMusician.setEnabled(
				listMusicians.getSelectedIndex() != -1 && !textFieldNameMusician.getText().trim().isEmpty());
	}

	private void btnAddAlbumEnabler() {
		btnAddAlbum.setEnabled(listMusicians.getSelectedIndex() != -1 && !textFieldIdAlbum.getText().trim().isEmpty()
				&& !textFieldTitleAlbum.getText().trim().isEmpty());
	}

	private void btnDeleteAlbumEnabler() {
		btnDeleteAlbum.setEnabled(listMusicians.getSelectedIndex() != -1 && listAlbums.getSelectedIndex() != -1);
	}

	private void btnUpdateAlbumEnabler() {
		btnUpdateAlbum.setEnabled(listMusicians.getSelectedIndex() != -1 && listAlbums.getSelectedIndex() != -1
				&& !textFieldTitleAlbum.getText().trim().isEmpty());
	}

	private String displayMusician(Musician musician) {
		return musician.getId() + " - " + musician.getName();
	}

	private void resetErrorLabel() {
		labelError.setText(" ");
	}

	private void removeMusician(Musician musician) {
		musicianListModel.removeElement(musician);
		albumListModel.clear();
	}

	@Override
	public void showAllMusicians(List<Musician> musicians) {
		musicians.stream().forEach(musicianListModel::addElement);
	}

	@Override
	public void musicianAdded(Musician added) {
		musicianListModel.addElement(added);
		resetErrorLabel();
	}

	@Override
	public void showErrorDuplicateMusicianId(String message, Musician existingMusician) {
		labelError.setText(message + ": " + displayMusician(existingMusician));
	}

	@Override
	public void musicianRemoved(Musician removed) {
		removeMusician(removed);
		resetErrorLabel();
	}

	@Override
	public void showErrorMusicianNotFound(String message, Musician selectedMusician) {
		labelError.setText(message + ": " + displayMusician(selectedMusician));
		removeMusician(selectedMusician);
	}

	@Override
	public void musicianUpdated(Musician toUpdate, Musician updated) {
		musicianListModel.set(musicianListModel.indexOf(toUpdate), updated);
		resetErrorLabel();
	}

	@Override
	public void showAllAlbums(List<Album> albums) {
		// TODO Auto-generated method stub

	}

	@Override
	public void albumAdded(Album added) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showErrorDuplicateAlbumId(String message, Album existingAlbum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void albumRemoved(Album removed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showErrorAlbumNotFound(String message, Album selectedAlbum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void albumUpdated(Album toUpdate, Album updated) {
		// TODO Auto-generated method stub

	}

}
