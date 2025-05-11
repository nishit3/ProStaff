import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AdminAddTeamComponent } from '../admin-add-team/admin-add-team.component';
import { Router } from '@angular/router';
import { Team, TeamsService } from '../services/teams.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-admin-teams',
  standalone: false,
  templateUrl: './admin-teams.component.html',
  styleUrl: './admin-teams.component.css'
})
export class AdminTeamsComponent implements OnInit {
  constructor(public dialog: MatDialog, public router: Router, private authService: AuthServiceService, private teamsService: TeamsService) { }
  adminEmail: string | null = ''
  searchTerm: string = '';
  teams: Team[] = []

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return;
    this.getTeams();
  }

  getTeams() {
    if (!this.adminEmail) return;
    this.teamsService.getAllTeams(this.adminEmail).subscribe((teams) => {
      this.teams = teams
    });
  }



  viewEmployees(teamId: number) {
    // Store department ID in local storage
    localStorage.setItem('selectedTeamId', teamId.toString());

    // Navigate to employee management page
    this.router.navigate(['/adminEmpManagement']);
  }

  get filteredTeams() {
    return this.teams.filter(team =>
      team.name.toLowerCase().includes(this.searchTerm.toLowerCase()) || team.description.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  deleteTeam(teamId: number, index: number) {
    if (!this.adminEmail) return;

    this.teamsService.deleteTeam(teamId, this.adminEmail).subscribe(
      (response) => {
        if (response) {
          this.getTeams() // Update UI immediately
        } else {
          alert('Failed to delete team.');
        }
      },
      (error) => {
        console.error('Delete team error:', error);
        alert('An error occurred while deleting the team.');
      }
    );
  }

  addTeams() {
    const dialogRef = this.dialog.open(AdminAddTeamComponent, {
      width: '500px'
    });

    // Receive Data from Dialog when the Create button is clicked
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const newTeam = {
          ...result,
          adminEmail: this.adminEmail
        };
        this.teamsService.addTeam(newTeam).subscribe(
          (response: boolean) => {
            if (response) {
              this.getTeams();
            } else {
              console.log('Failed to add team');
            }
          },
          (error) => {
            console.error('API error:', error);
          }
        );
      }
    });
  }
}
