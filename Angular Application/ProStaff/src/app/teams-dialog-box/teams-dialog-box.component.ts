import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TeamsService } from '../services/teams.service';
import { AuthServiceService } from '../services/auth-service.service';
import { Team } from '../services/teams.service';

@Component({
  selector: 'app-teams-dialog-box',
  standalone: false,
  templateUrl: './teams-dialog-box.component.html',
  styleUrl: './teams-dialog-box.component.css'
})
export class TeamsDialogBoxComponent implements OnInit {
  teamsList: { id: number; name: string; selected: boolean }[] = [];

  constructor(
    public dialogRef: MatDialogRef<TeamsDialogBoxComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private teamService: TeamsService,
    private authService: AuthServiceService
  ) { }

  ngOnInit() {
    const adminEmail = this.authService.getLoggedInEmail();
    if (!adminEmail) return;

    this.teamService.getAllTeams(adminEmail).subscribe((teams) => {
      this.teamsList = teams.map(team => ({
        id: team.id,
        name: team.name,
        selected: this.data?.selectedTeams?.some(
          (sel: any) => sel.id === team.id
        ) || false
      }));
    });
  }
  isAllSelected(): boolean {
    return this.teamsList.every(team => team.selected);
  }

  toggleSelectAll(): void {
    const allSelected = this.isAllSelected();
    this.teamsList.forEach(team => team.selected = !allSelected);
  }

  submitSelection(): void {
    const selectedTeams = this.teamsList
      .filter(team => team.selected)
      .map(team => ({ id: team.id, name: team.name }));
  
    this.dialogRef.close(selectedTeams);
  }
  


  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
