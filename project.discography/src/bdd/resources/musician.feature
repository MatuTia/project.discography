Feature: Discography View, Musician
	Specifications of the behavior of the musician part of the discography view

	Background: 
		Given The database contains a few musicians
		And The discography view is shown

	Scenario: Add a new musician
		Given The user provides the musician data in the text fields
		When The user clicks the "Add Musician" button
		Then The musician list contains the new musician

	Scenario: Add a new musician, with existing id
		Given The user provides the musician data in the text fields, specifying an existing id
		When The user clicks the "Add Musician" button
		Then An error is shown containing the name of the existing musician

	Scenario: Delete a musician
		Given The user selects a musician from the musician list
		When The user clicks the "Delete Musician" button
		Then The selected musician is removed from the musician list
		And The album list is cleared

	Scenario: Delete a not existing musician
		Given The user selects a musician from the musician list
		But The selected musician is in the meantime removed from the database
		When The user clicks the "Delete Musician" button
		Then An error is shown containing the name of the selected musician
		And The selected musician is removed from the musician list
		And The album list is cleared

	Scenario: Update the name of a musician
		Given The user selects a musician from the musician list
		And The user provides the updated name of the musician in the text field
		When The user clicks the "Update Musician" button
		Then The selected musician is updated with the new name

	Scenario: Update the name of a not existing musician
		Given The user selects a musician from the musician list
		And The user provides the updated name of the musician in the text field
		But The selected musician is in the meantime removed from the database
		When The user clicks the "Update Musician" button
		Then An error is shown containing the name of the selected musician
		And The selected musician is removed from the musician list
		And The album list is cleared

	Scenario: Show a musician discography
		When The user selects a musician from the musician list
		Then The discography of the selected musician is show in the album list

	Scenario: Show a not existing musician discography
		Given The musician who will be selected was removed the database
		When The user selects a musician from the musician list
		Then An error is shown containing the name of the selected musician
		And The selected musician is removed from the musician list
		And The album list is cleared
