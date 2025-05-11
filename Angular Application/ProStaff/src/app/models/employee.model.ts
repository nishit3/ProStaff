export interface AddEmployeeRequest {
    employeeRegistrationDetail: {
      email: string;
      fullName: string;
      adminEmail: string;
      gender: string;
      skills: string[];
      reportingManager: string;
      phoneNumber: string;
      dob: string;
      joiningDate: string;
      emergencyContact: string;
      bankDetail: {
        bankName: string;
        ifscCode: string;
        accountNumber: string;
        branch: string;
      };
      department: number;
      designation: number;
      teams: number[];
      address: string;
    };
    profileImage: string;
  }
  