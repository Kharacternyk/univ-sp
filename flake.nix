{
  outputs = { self, nixpkgs, flake-utils }: flake-utils.lib.eachDefaultSystem (system:
    with import nixpkgs { inherit system; };
    {
      devShell = mkShell {
        packages = [
          jdk17_headless
          gradle
        ];
      };
    }
  );
}
