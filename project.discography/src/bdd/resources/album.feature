Feature: Discography View, Album
	Specifications of the behavior of the album part of the discography view

	Background: 
		Given The database contains a few musicians
		And The database contains a few albums
		And The discography view is shown

	Scenario: Add a new album
		Given The user selects a musician from the musician list
		And The user provides the album data in the text fields
		When The user clicks the "Add Album" button
		Then The albm list contains the new album

	Scenario: Add a new album, with existing id
		Given The user selects a musician from the musician list
		And The user provides the album data in the text fields, specifying an existing id
		When The user clicks the "Add Album" button
		Then An error is shown containing the title of the existing album

	Scenario: Add a new album to a not existing musician
		Given The user selects a musician from the musician list
		And The user provides the album data in the text fields
		But The selected musician is in the meantime removed from the database
		When The user clicks the "Add Album" button
		Then An error is shown containing the name of the selected musician
		And The selected musician is removed from the musician list
		And The album list is cleared

	Scenario: Delete an album
		Given The user selects a musician from the musician list
		And The user selects an album from the album list
		When The user clicks the "Delete Album" button
		Then The selected album is removed from the album list

	Scenario: Delete a not existing album
		Given The user selects a musician from the musician list
		And The user selects an album from the album list
		But The selected album is in the meantime removed from the database
		When The user clicks the "Delete Album" button
		Then An error is shown containing the title of the selected album
		And The selected album is removed from the album list

	Scenario: Delete an album to a not existing musician
		Given The user selects a musician from the musician list
		And The user selects an album from the album list
		But The selected musician is in the meantime removed from the database
		When The user clicks the "Delete Album" button
		Then An error is shown containing the name of the selected musician
		And The selected musician is removed from the musician list
		And The album list is cleared

	Scenario: Update the title of an album
		Given The user selects a musician from the musician list
		And The user selects an album from the album list
		And The user provides the updated title of the album in the text field
		When The user clicks the "Update Album" button
		Then The selected album is updated with the new title

	Scenario: Update the title of a not existing album
		Given The user selects a musician from the musician list
		And The user selects an album from the album list
		And The user provides the updated title of the album in the text field
		But The selected album is in the meantime removed from the database
		When The user clicks the "Update Album" button
		Then An error is shown containing the title of the selected album
		And The selected album is removed from the album list

	Scenario: Update the title of album to a not existing musician
		Given The user selects a musician from the musician list
		And The user selects an album from the album list
		And The user provides the updated title of the album in the text field
		But The selected musician is in the meantime removed from the database
		When The user clicks the "Update Album" button
		Then An error is shown containing the name of the selected musician
		And The selected musician is removed from the musician list
		And The album list is cleared
