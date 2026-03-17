export default function Button({ children, loading, ...props }) {
  return (
    <button
      {...props}
      className="
        w-full
        bg-blue-600 text-white
        py-2 rounded-lg
        hover:bg-blue-700
        disabled:opacity-50
      "
    >
      {loading ? "Please wait..." : children}
    </button>
  );
}
