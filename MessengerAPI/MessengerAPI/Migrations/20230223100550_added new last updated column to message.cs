using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MessengerAPI.Migrations
{
    /// <inheritdoc />
    public partial class addednewlastupdatedcolumntomessage : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<DateTime>(
                name: "UpdatedTime",
                table: "Messages",
                type: "datetime2",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "UpdatedTime",
                table: "Messages");
        }
    }
}
