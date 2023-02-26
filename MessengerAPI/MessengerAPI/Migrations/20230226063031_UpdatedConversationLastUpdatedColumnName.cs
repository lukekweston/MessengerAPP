using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MessengerAPI.Migrations
{
    /// <inheritdoc />
    public partial class UpdatedConversationLastUpdatedColumnName : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "UpdatedTime",
                table: "Conversations",
                newName: "LastUpdated");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "LastUpdated",
                table: "Conversations",
                newName: "UpdatedTime");
        }
    }
}
