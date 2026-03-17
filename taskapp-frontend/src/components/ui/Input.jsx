export default function Input({
  label,
  type = "text",
  value,
  onChange,
  placeholder
}) {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-sm text-gray-600">{label}</label>

      <input
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className="
          px-3 py-2
          border rounded-lg
          focus:outline-none
          focus:ring-2 focus:ring-blue-500
        "
      />
    </div>
  );
}
